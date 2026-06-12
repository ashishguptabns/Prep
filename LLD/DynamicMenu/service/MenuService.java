package LLD.DynamicMenu.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import LLD.DynamicMenu.exception.OrderValidationException;
import LLD.DynamicMenu.model.Dish;
import LLD.DynamicMenu.model.DishIngredientRule;
import LLD.DynamicMenu.model.Ingredient;
import LLD.DynamicMenu.repo.InventoryRepository;
import LLD.DynamicMenu.repo.MenuRepository;

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
        Map<String, Ingredient> map = inventoryRepository.getAllIngredients();
        for (String key : map.keySet()) {
            Ingredient ing = map.get(key);
            if (ing.getQuantity() > 0) {
                System.out.println("Ingredient - " + ing.getName() + " Quantity - " + ing.getQuantity());
            }
        }
    }

    public void getAvailableMenu() {
        List<String> availableDishes = new ArrayList<>();
        Map<String, Dish> allDishes = menuRepository.getAllDishes();

        for (Dish dish : allDishes.values()) {
            if (calculateMaxAvailableQuantity(dish) > 0) {
                availableDishes.add(dish.getName());
            }
        }
        System.out.println("Available dishes: " + availableDishes);
    }

    public void orderDish(String dishName, int orderQty) throws OrderValidationException {
        Dish dish = menuRepository.getDish(dishName);
        if (dish == null) {
            throw new OrderValidationException("Dish " + dishName + " does not exist");
        }

        List<Ingredient> ingredientsToLock = new ArrayList<>();
        for (DishIngredientRule req : dish.getRules()) {
            Ingredient invIngredient = inventoryRepository.getIngredient(req.getIngredientName());
            if (invIngredient != null) {
                ingredientsToLock.add(invIngredient);
            }
        }

        ingredientsToLock.sort((a, b) -> a.getName().compareTo(b.getName()));

        try {
            for (Ingredient ing : ingredientsToLock) {
                if (!ing.getLock().tryLock()) {
                    throw new OrderValidationException("Ingredient " + ing.getName() + " is not available");
                }
            }

            int maxAvailable = calculateMaxAvailableQuantity(dish);

            if (maxAvailable == 0) {
                throw new OrderValidationException("Dish " + dishName + " is unavailable");
            }

            if (orderQty > maxAvailable) {
                throw new OrderValidationException("Can't place your order, only "
                        + maxAvailable + " qty of " + dishName + " is available");
            }

            for (DishIngredientRule req : dish.getRules()) {
                Ingredient invIngredient = inventoryRepository.getIngredient(req.getIngredientName());
                int totalDeduction = req.getRequiredQuantity() * orderQty;
                invIngredient.deduceQuantity(totalDeduction);
            }
        } finally {
            for (int i = ingredientsToLock.size() - 1; i >= 0; i--) {
                try {
                    ingredientsToLock.get(i).getLock().unlock();
                } catch (Exception e) {
                }
            }
        }
    }

    private int calculateMaxAvailableQuantity(Dish dish) {
        int max = Integer.MAX_VALUE;

        for (DishIngredientRule req : dish.getRules()) {
            Ingredient ing = inventoryRepository.getIngredient(req.getIngredientName());
            if (ing == null || ing.getQuantity() < req.getRequiredQuantity()) {
                return 0;
            }
            int limit = ing.getQuantity() / req.getRequiredQuantity();
            if (limit < max) {
                max = limit;
            }
        }
        return max == Integer.MAX_VALUE ? 0 : max;
    }
}
