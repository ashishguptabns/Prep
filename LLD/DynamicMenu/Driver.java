package LLD.DynamicMenu;

import LLD.DynamicMenu.exception.*;
import LLD.DynamicMenu.model.*;
import LLD.DynamicMenu.repo.*;
import LLD.DynamicMenu.service.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

public class Driver {

    public static void main(String[] args) {
        Driver app = new Driver();
        app.run();
    }

    void run() {
        InventoryRepository inventoryRepository = new InventoryRepository();
        MenuRepository menuRepository = new MenuRepository();
        MenuService menuService = new MenuService(inventoryRepository, menuRepository);

        System.out.println("--- STEP 1: ADDING INGREDIENTS ---");
        menuService.addIngredients(Arrays.asList(
                new Ingredient("11", 2),
                new Ingredient("12", 2),
                new Ingredient("13", 1)
        ));
        System.out.println("Ingredient(s) added successfully.\n");

        System.out.println("--- STEP 2: ADDING DISHES ---");
        // Supporting Bonus: A dish maps to a list of requirements
        menuService.addDish(new Dish("D1", Collections.singletonList(new DishIngredientRequirement("11", 2))));
        menuService.addDish(new Dish("D2", Collections.singletonList(new DishIngredientRequirement("11", 1))));
        menuService.addDish(new Dish("D3", Collections.singletonList(new DishIngredientRequirement("12", 2))));
        menuService.addDish(new Dish("D4", Collections.singletonList(new DishIngredientRequirement("13", 1))));
        System.out.println("Dishes added.\n");

        System.out.println("--- STEP 3: GET MENU ---");
        List<String> menu = menuService.getAvailableMenu();
        System.out.println("Available dishes: " + menu + "\n");

        System.out.println("--- STEP 4: ORDERING DISH D1 (QTY 1) ---");
        try {
            menuService.orderDish("D1", 1);
            System.out.println("Order placed successfully\n");
        } catch (OrderValidationException e) {
            System.out.println(e.getMessage() + "\n");
        }

        System.out.println("--- STEP 5: GET MENU AFTER D1 (Ingredient 11 dropped to 0) ---");
        // D1 and D2 both relied on '11'. Since 11's quantity is now 0, both should automatically be omitted.
        System.out.println("Available dishes: " + menuService.getAvailableMenu() + "\n");

        System.out.println("--- STEP 6: ERRONEOUS ORDER FOR D3 (Ordering 2 when only 1 batch is possible) ---");
        try {
            menuService.orderDish("D3", 2);
        } catch (OrderValidationException e) {
            System.out.println(e.getMessage() + "\n");
        }

        System.out.println("--- STEP 7: PRINT INVENTORY STATUS ---");
        menuService.printAvailableIngredients();
        System.out.println();

        System.out.println("--- STEP 8: BONUS REQUIREMENT DEMO (Multi-ingredient dish) ---");
        // Creating a dynamic dish "D_BONUS" that requires both Ingredient "12" and "13"
        menuService.addDish(new Dish("D_BONUS", Arrays.asList(
                new DishIngredientRequirement("12", 1),
                new DishIngredientRequirement("13", 1)
        )));
        System.out.println("Menu with bonus multi-ingredient dish included: " + menuService.getAvailableMenu() + "\n");

        System.out.println("--- STEP 9: CONCURRENCY UNIT TEST SIMULATION ---");
        // Setting up a distinct environment to isolate the specific concurrency test condition
        InventoryRepository concurrencyInvRepo = new InventoryRepository();
        MenuRepository concurrencyMenuRepo = new MenuRepository();
        MenuService concurrencyService = new MenuService(concurrencyInvRepo, concurrencyMenuRepo);

        // Scenario setup: Only 1 batch of ingredient 'X' is available.
        concurrencyInvRepo.addIngredient("X", 1);
        // Dish D5 requires 1 batch of ingredient 'X'
        concurrencyMenuRepo.addDish(new Dish("D5", Collections.singletonList(new DishIngredientRequirement("X", 1))));

        int threadCount = 2;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        AtomicInteger successCounter = new AtomicInteger(0);
        AtomicInteger failureCounter = new AtomicInteger(0);

        System.out.println("Triggering 2 simultaneous order transactions for D5 (Only 1 quantity available)...");
        for (int i = 0; i < threadCount; i++) {
            executorService.execute(() -> {
                try {
                    concurrencyService.orderDish("D5", 1);
                    successCounter.incrementAndGet();
                    System.out.println("[Thread Success] Order placed successfully.");
                } catch (OrderValidationException e) {
                    failureCounter.incrementAndGet();
                    System.out.println("[Thread Failed] Error: " + e.getMessage());
                }
            });
        }

        executorService.shutdown();
        try {
            if (executorService.awaitTermination(3, TimeUnit.SECONDS)) {
                System.out.println("\n--- CONCURRENCY RESULTS ---");
                System.out.println("Total Successful Orders: " + successCounter.get());
                System.out.println("Total Failed Orders: " + failureCounter.get());
                if (successCounter.get() == 1 && failureCounter.get() == 1) {
                    System.out.println("CONCURRENCY VERIFICATION PASSED: Only 1 transaction was successfully fulfilled.");
                } else {
                    System.out.println("CONCURRENCY VERIFICATION FAILED.");
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
