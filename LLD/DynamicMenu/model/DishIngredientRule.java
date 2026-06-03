package LLD.DynamicMenu.model;

public class DishIngredientRule {

    private final String ingredientName;
    private final int requiredQuantity;

    public DishIngredientRule(String ingredientName, int requiredQuantity) {
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
