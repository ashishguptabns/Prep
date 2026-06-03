package LLD.DynamicMenu.repo;

import LLD.DynamicMenu.model.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MenuRepository {

    private final Map<String, Dish> dishMap = new ConcurrentHashMap<>();

    public void addDish(Dish dish) {
        dishMap.put(dish.getName(), dish);
    }

    public Map<String, Dish> getAllDishes() {
        return dishMap;
    }

    public Dish getDish(String name) {
        return dishMap.get(name);
    }
}
