package LLD.DynamicMenu.service;

import LLD.DynamicMenu.exception.*;
import LLD.DynamicMenu.model.*;
import LLD.DynamicMenu.repo.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MenuService {

    private final InventoryRepository inventoryRepository;
    private final MenuRepository menuRepository;

    public MenuService(InventoryRepository inventoryRepository, MenuRepository menuRepository) {
        this.inventoryRepository = inventoryRepository;
        this.menuRepository = menuRepository;
    }

    public void addIngredients(List<Ingredient> ingredients) {
        for (Ingredient ing : ingredients) {
            inventoryRepository.addIngredient(ing.getName(), ing.getQuantity());
        }
    }

    public void addDishes(List<Dish> dishes) {
        for (Dish d : dishes) {
            menuRepository.addDish(d);
        }
    }

    public void addDish(Dish d) {
        menuRepository.addDish(d);
    }

    public void printAvailableIngredients() {
        inventoryRepository.getAllIngredients().forEach((name, ingredient) -> {
            if (ingredient.getQuantity() > 0) {
                System.out.println("IngredientName - " + name + " Quantity - " + ingredient.getQuantity());
            }
        });
    }

    public List<String> getAvailableMenu() {
        List<String> availableDishes = new ArrayList<>();
        Map<String, Dish> allDishes = menuRepository.getAllDishes();

        for (Dish dish : allDishes.values()) {
            if (calculateMaxAvailableQuantity(dish) > 0) {
                availableDishes.add(dish.getName());
            }
        }
        return availableDishes;
    }

    public void orderDish(String dishName, int orderQty) throws OrderValidationException {
        Dish dish = menuRepository.getDish(dishName);
        if (dish == null) {
            throw new OrderValidationException("Dish " + dishName + " does not exist");
        }

        synchronized (inventoryRepository) {
            int maxAvailable = calculateMaxAvailableQuantity(dish);

            if (maxAvailable == 0) {
                throw new OrderValidationException("Dish " + dishName + " is unavailable");
            }

            if (orderQty > maxAvailable) {
                throw new OrderValidationException("Can't place your order " + maxAvailable + " qty of " + dishName + " is available");
            }

            for (DishIngredientRule req : dish.getRequirements()) {
                Ingredient invIngredient = inventoryRepository.getIngredient(req.getIngredientName());
                int totalDeduction = req.getRequiredQuantity() * orderQty;
                invIngredient.deduceQuantity(totalDeduction);
            }
        }
    }

    private int calculateMaxAvailableQuantity(Dish dish) {
        int maxPossible = Integer.MAX_VALUE;

        for (DishIngredientRule req : dish.getRequirements()) {
            Ingredient invIngredient = inventoryRepository.getIngredient(req.getIngredientName());
            if (invIngredient == null || invIngredient.getQuantity() < req.getRequiredQuantity()) {
                return 0;
            }
            int dynamicLimit = invIngredient.getQuantity() / req.getRequiredQuantity();
            if (dynamicLimit < maxPossible) {
                maxPossible = dynamicLimit;
            }
        }
        return maxPossible == Integer.MAX_VALUE ? 0 : maxPossible;
    }

    public void printMenu() {
        System.out.println("Available dishes: " + getAvailableMenu());
    }

}
