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
        runNormal();
        runConcurrent();
    }

    private List<Ingredient> getDummyIngreds() {
        return List.of(
                new Ingredient("11", 2),
                new Ingredient("12", 2),
                new Ingredient("13", 1)
        );
    }

    private List<Dish> getDummyDishes() {
        return List.of(
                new Dish("D1", List.of(new DishIngredientRule("11", 2))),
                new Dish("D2", List.of(new DishIngredientRule("12", 2))),
                new Dish("D3", List.of(new DishIngredientRule("13", 2))),
                new Dish("D4", List.of(new DishIngredientRule("14", 2)))
        );
    }

    private void runNormal() {

        System.out.println("Normal");

        InventoryRepository inventoryRepository = new InventoryRepository();
        MenuRepository menuRepository = new MenuRepository();
        MenuService menuService = new MenuService(inventoryRepository, menuRepository);

        menuService.addIngredients(getDummyIngreds());
        menuService.addDishes(getDummyDishes());

        menuService.printMenu();
        menuService.printAvailableIngredients();
        try {
            menuService.orderDish("D1", 1);
            System.out.println("Order placed D1");
        } catch (OrderValidationException e) {
            System.err.println("Exception " + e.getMessage());
        }

        menuService.printMenu();
        menuService.printAvailableIngredients();
        try {
            menuService.orderDish("D3", 2);
        } catch (OrderValidationException e) {
            System.err.println("Exception " + e.getMessage());
        }

        menuService.printMenu();
        menuService.printAvailableIngredients();
        try {
            menuService.orderDish("D2", 2);
        } catch (OrderValidationException e) {
            System.err.println("Exception " + e.getMessage());
        }

        menuService.printMenu();
        menuService.printAvailableIngredients();
        menuService.addDish(new Dish("Bonus", List.of(
                new DishIngredientRule("12", 1),
                new DishIngredientRule("13", 1)
        )));
        menuService.printMenu();
        menuService.printAvailableIngredients();
    }

    private void runConcurrent() {
        System.out.println("Concurrent");

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
            endLatch.await();
            executor.shutdown();
            if (successCounter.get() == 1 && failureCounter.get() == threadCount - successCounter.get()) {
                System.out.println("Pass");
            } else {
                System.out.println("Fail");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

}
