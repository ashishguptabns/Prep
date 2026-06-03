package LLD.DynamicMenu.model;

import java.util.List;

public class Dish {

    private final String name;
    private final List<DishIngredientRule> requirements;

    public Dish(String name, List<DishIngredientRule> requirements) {
        this.name = name;
        this.requirements = requirements;
    }

    public String getName() {
        return name;
    }

    public List<DishIngredientRule> getRequirements() {
        return requirements;
    }
}
