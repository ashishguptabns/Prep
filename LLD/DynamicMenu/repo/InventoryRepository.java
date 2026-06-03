package LLD.DynamicMenu.repo;

import LLD.DynamicMenu.model.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class InventoryRepository {

    private final Map<String, Ingredient> map = new ConcurrentHashMap<>();

    public void addIngredient(String name, int quantity) {
        map.compute(name, (key, existingIngredient) -> {
            if (existingIngredient == null) {
                return new Ingredient(name, quantity);
            } else {
                existingIngredient.addQuantity(quantity);
                return existingIngredient;
            }
        });
    }

    public Map<String, Ingredient> getAllIngredients() {
        return map;
    }

    public Ingredient getIngredient(String name) {
        return map.get(name);
    }
}
