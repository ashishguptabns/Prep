package LLD.DynamicMenu.model;

import java.util.List;

public class Dish {

    private final String name;
    private final List<DishIngredientRule> rules;

    public Dish(String name, List<DishIngredientRule> rules) {
        this.name = name;
        this.rules = rules;
    }

    public String getName() {
        return name;
    }

    public List<DishIngredientRule> getRules() {
        return rules;
    }
}
