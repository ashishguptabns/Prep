package LLD.DynamicMenu.repo;

import LLD.DynamicMenu.model.*;
import java.util.Map;
import java.util.ConcurrentModificationException;
import java.util.concurrent.ConcurrentHashMap;

public class InventoryRepository {

    private final Map<String, Ingredient> ingredientMap = new ConcurrentHashMap<>();

    public void addIngredient(String name, int quantity) {
        ingredientMap.compute(name, (key, existing) -> {
            if (existing == null) {
                return new Ingredient(name, quantity);
            } else {
                existing.addQuantity(quantity);
                return existing;
            }
        });
    }

    public Map<String, Ingredient> getAllIngredients() {
        return ingredientMap;
    }

    public Ingredient getIngredient(String name) {
        return ingredientMap.get(name);
    }
}
