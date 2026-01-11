package LLD;

// Strategy Interface
interface PricingStrategy {
    double apply(double price);
}

// Concrete Strategies
class PrimeStrategy implements PricingStrategy {
    public double apply(double p) {
        return p * 0.90;
    }
}

class GuestStrategy implements PricingStrategy {
    public double apply(double p) {
        return p;
    }
}

// Factory for Strategy selection
class StrategyFactory {
    public static PricingStrategy get(String type) {
        return type.equals("PRIME") ? new PrimeStrategy() : new GuestStrategy();
    }
}

// Decorator for layering (e.g., Coupons)
abstract class PriceDecorator implements PricingStrategy {
    protected PricingStrategy wrapped;

    PriceDecorator(PricingStrategy w) {
        this.wrapped = w;
    }
}

class CouponDecorator extends PriceDecorator {
    CouponDecorator(PricingStrategy w) {
        super(w);
    }

    public double apply(double p) {
        return wrapped.apply(p) - 5.0;
    } // Flat $5 off
}

// Additional Strategy
class BusinessStrategy implements PricingStrategy {
    public double apply(double p) {
        return p * 0.85;
    } // 15% bulk discount
}

// Additional Decorator
class TaxDecorator extends PriceDecorator {
    TaxDecorator(PricingStrategy w) {
        super(w);
    }

    public double apply(double p) {
        return wrapped.apply(p) * 1.08;
    } // 8% Tax
}

public class DesignPattern {

    public static void main(String[] args) {
        double itemPrice = 100.0;

        // 1. Use Factory to pick the base strategy
        PricingStrategy baseStrategy = StrategyFactory.get("PRIME");

        // 2. Wrap it with Decorators for additional modifiers
        // This layers: (Base Strategy) -> Coupon
        PricingStrategy discountedPrice = new CouponDecorator(baseStrategy);

        // 3. Execute the chain
        double total = discountedPrice.apply(itemPrice);

        System.out.println("Original Price: $" + itemPrice);
        System.out.println("Final Price after Prime (10%) and Coupon ($5): $" + total);

        // Updated Main usage
        PricingStrategy order = new TaxDecorator(new CouponDecorator(new BusinessStrategy()));
        System.out.println("Final: " + (int) order.apply(itemPrice)); // ((200 * 0.85) - 5) * 1.08
    }
}
