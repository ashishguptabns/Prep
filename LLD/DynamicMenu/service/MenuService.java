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

    // 1. API to add ingredients with quantity
    public void addIngredients(List<Ingredient> ingredients) {
        for (Ingredient ing : ingredients) {
            inventoryRepository.addIngredient(ing.getName(), ing.getQuantity());
        }
    }

    // 2. API to get available ingredients with respective quantities
    public void printAvailableIngredients() {
        System.out.println("IngredientName    Quantity");
        inventoryRepository.getAllIngredients().forEach((name, ingredient) -> {
            if (ingredient.getQuantity() > 0) {
                System.out.println(name + "                " + ingredient.getQuantity());
            }
        });
    }

    // 3. API to add dishes with required ingredients
    public void addDish(Dish dish) {
        menuRepository.addDish(dish);
    }

    // 4. API to fetch menu cards with the available dishes
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

    // 5 & 6 & 7 & 8. API to order dishes with strict multi-threaded thread-safety and validations
    public void orderDish(String dishName, int orderQty) throws OrderValidationException {
        Dish dish = menuRepository.getDish(dishName);
        if (dish == null) {
            throw new OrderValidationException("Dish " + dishName + " does not exist.");
        }

        // Global lock on inventory or fine-grained sorted locking on required ingredients 
        // prevents deadlocks and enforces strict transactional synchronization.
        synchronized (inventoryRepository) {
            int maxAvailable = calculateMaxAvailableQuantity(dish);

            if (maxAvailable == 0) {
                throw new OrderValidationException("Dish " + dishName + " is currently unavailable.");
            }

            if (orderQty > maxAvailable) {
                throw new OrderValidationException("Couldn't place your order, only " + maxAvailable + " qty of " + dishName + " is available.");
            }

            // Deduct the ingredients safely
            for (DishIngredientRequirement req : dish.getRequirements()) {
                Ingredient invIngredient = inventoryRepository.getIngredient(req.getIngredientName());
                int totalDeduction = req.getRequiredQuantity() * orderQty;
                invIngredient.deduceQuantity(totalDeduction);
            }
        }
    }

    /**
     * Helper to compute the maximum possible portions of a dish given current
     * inventory levels. Works perfectly for single-ingredient dishes as well as
     * multi-ingredient setups (Bonus).
     */
    private int calculateMaxAvailableQuantity(Dish dish) {
        int maxPossible = Integer.MAX_VALUE;

        for (DishIngredientRequirement req : dish.getRequirements()) {
            Ingredient invIngredient = inventoryRepository.getIngredient(req.getIngredientName());
            if (invIngredient == null || invIngredient.getQuantity() < req.getRequiredQuantity()) {
                return 0; // Missing or insufficient base ingredients
            }
            int dynamicLimit = invIngredient.getQuantity() / req.getRequiredQuantity();
            if (dynamicLimit < maxPossible) {
                maxPossible = dynamicLimit;
            }
        }
        return maxPossible == Integer.MAX_VALUE ? 0 : maxPossible;
    }
}
