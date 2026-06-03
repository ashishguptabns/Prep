package LLD.DynamicMenu.model;

public class DishIngredientRequirement {

    private final String ingredientName;
    private final int requiredQuantity;

    public DishIngredientRequirement(String ingredientName, int requiredQuantity) {
        this.ingredientName = ingredientName;
        this.requiredQuantity = requiredQuantity;
    }

    public String getIngredientName() {
        return ingredientName;
    }

    public int getRequiredQuantity() {
        return requiredQuantity;
    }
}
