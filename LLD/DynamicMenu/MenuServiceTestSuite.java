package LLD.DynamicMenu;

import LLD.DynamicMenu.exception.OrderValidationException;
import LLD.DynamicMenu.model.*;
import LLD.DynamicMenu.repo.*;
import LLD.DynamicMenu.service.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class MenuServiceTestSuite {

    public void runAllTests() {
        testAddAndPrintIngredients();
        testAddDishesAndGetMenu();
        testOrderPlacingAndCascadingMenuUpdates();
        testValidationFailureOverordering();
        testBonusRequirementMultiIngredientDish();
        testConcurrentOrdersSafety();
    }

    private void testAddAndPrintIngredients() {
        System.out.println("--- TEST: ADD AND PRINT INGREDIENTS ---");
        InventoryRepository inventoryRepository = new InventoryRepository();
        MenuRepository menuRepository = new MenuRepository();
        MenuService menuService = new MenuService(inventoryRepository, menuRepository);

        menuService.addIngredients(Arrays.asList(
                new Ingredient("11", 2),
                new Ingredient("12", 2),
                new Ingredient("13", 1)
        ));
        menuService.printAvailableIngredients();
        System.out.println();
    }

    private void testAddDishesAndGetMenu() {
        System.out.println("--- TEST: ADD DISHES AND GET MENU ---");
        InventoryRepository inventoryRepository = new InventoryRepository();
        MenuRepository menuRepository = new MenuRepository();
        MenuService menuService = new MenuService(inventoryRepository, menuRepository);

        inventoryRepository.addIngredient("11", 2);
        inventoryRepository.addIngredient("12", 2);
        inventoryRepository.addIngredient("13", 1);

        menuService.addDish(new Dish("D1", Collections.singletonList(new DishIngredientRule("11", 2))));
        menuService.addDish(new Dish("D2", Collections.singletonList(new DishIngredientRule("11", 1))));
        menuService.addDish(new Dish("D3", Collections.singletonList(new DishIngredientRule("12", 2))));
        menuService.addDish(new Dish("D4", Collections.singletonList(new DishIngredientRule("13", 1))));

        List<String> availableMenu = menuService.getAvailableMenu();
        System.out.println("Expected: [D1, D2, D3, D4]");
        System.out.println("Actual: " + availableMenu);
        System.out.println();
    }

    private void testOrderPlacingAndCascadingMenuUpdates() {
        System.out.println("--- TEST: ORDER PLACING & CASCADING MENU UPDATES ---");
        InventoryRepository inventoryRepository = new InventoryRepository();
        MenuRepository menuRepository = new MenuRepository();
        MenuService menuService = new MenuService(inventoryRepository, menuRepository);

        inventoryRepository.addIngredient("11", 2);
        inventoryRepository.addIngredient("12", 2);
        inventoryRepository.addIngredient("13", 1);

        menuService.addDish(new Dish("D1", Collections.singletonList(new DishIngredientRule("11", 2))));
        menuService.addDish(new Dish("D2", Collections.singletonList(new DishIngredientRule("11", 1))));

        try {
            menuService.orderDish("D1", 1);
            System.out.println("Order D1 placed successfully");
            List<String> updatedMenu = menuService.getAvailableMenu();
            System.out.println("Expected Menu: []");
            System.out.println("Actual Menu  : " + updatedMenu);
        } catch (OrderValidationException e) {
            System.out.println("Unexpected failure: " + e.getMessage());
        }
        System.out.println();
    }

    private void testValidationFailureOverordering() {
        System.out.println("--- TEST: VALIDATION FAILURE FOR OVERORDERING ---");
        InventoryRepository inventoryRepository = new InventoryRepository();
        MenuRepository menuRepository = new MenuRepository();
        MenuService menuService = new MenuService(inventoryRepository, menuRepository);

        inventoryRepository.addIngredient("12", 2);
        menuService.addDish(new Dish("D3", Collections.singletonList(new DishIngredientRule("12", 2))));

        try {
            menuService.orderDish("D3", 2);
            System.out.println("Fail - Order should not have been placed");
        } catch (OrderValidationException e) {
            System.out.println("Pass - Exception correctly caught: " + e.getMessage());
        }
        System.out.println();
    }

    private void testBonusRequirementMultiIngredientDish() {
        System.out.println("--- TEST: BONUS MULTI-INGREDIENT DISH ---");
        InventoryRepository inventoryRepository = new InventoryRepository();
        MenuRepository menuRepository = new MenuRepository();
        MenuService menuService = new MenuService(inventoryRepository, menuRepository);

        inventoryRepository.addIngredient("12", 2);
        inventoryRepository.addIngredient("13", 1);

        menuService.addDish(new Dish("BonusDish", Arrays.asList(
                new DishIngredientRule("12", 1),
                new DishIngredientRule("13", 1)
        )));

        System.out.println("Expected Menu: [BonusDish]");
        System.out.println("Actual Menu  : " + menuService.getAvailableMenu());

        try {
            menuService.orderDish("BonusDish", 1);
            System.out.println("BonusDish ordered successfully");
            System.out.println("Expected Menu after order: []");
            System.out.println("Actual Menu after order  : " + menuService.getAvailableMenu());
        } catch (OrderValidationException e) {
            System.out.println("Unexpected failure: " + e.getMessage());
        }
        System.out.println();
    }

    private void testConcurrentOrdersSafety() {
        System.out.println("--- TEST: CONCURRENT ORDERS SAFETY ---");
        InventoryRepository inventoryRepository = new InventoryRepository();
        MenuRepository menuRepository = new MenuRepository();
        MenuService menuService = new MenuService(inventoryRepository, menuRepository);

        inventoryRepository.addIngredient("X", 1);
        menuRepository.addDish(new Dish("D5", List.of(new DishIngredientRule("X", 1))));

        int threadCount = 5;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        AtomicInteger successCounter = new AtomicInteger(0);
        AtomicInteger failureCounter = new AtomicInteger(0);

        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch endLatch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            executor.execute(() -> {
                try {
                    startLatch.await();
                    menuService.orderDish("D5", 1);
                    successCounter.incrementAndGet();
                } catch (OrderValidationException e) {
                    failureCounter.incrementAndGet();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    endLatch.countDown();
                }
            });
        }

        startLatch.countDown();
        try {
            if (endLatch.await(5, TimeUnit.SECONDS)) {
                executor.shutdown();
                if (successCounter.get() == 1 && failureCounter.get() == threadCount - 1) {
                    System.out.println("Pass - Only 1 order succeeded out of " + threadCount);
                } else {
                    System.out.println("Fail - Successes: " + successCounter.get() + ", Failures: " + failureCounter.get());
                }
            } else {
                System.out.println("Fail - Test timed out execution deadlock checked");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        System.out.println();
    }

    public static void main(String[] args) {
        new MenuServiceTestSuite().runAllTests();
    }
}
