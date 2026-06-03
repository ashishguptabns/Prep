package LLD.DynamicMenu.model;

import java.util.List;

public class Dish {

    private final String name;
    // Handled as a list to support the Bonus Requirement (multiple ingredients per dish)
    private final List<DishIngredientRequirement> requirements;

    public Dish(String name, List<DishIngredientRequirement> requirements) {
        this.name = name;
        this.requirements = requirements;
    }

    public String getName() {
        return name;
    }

    public List<DishIngredientRequirement> getRequirements() {
        return requirements;
    }
}
