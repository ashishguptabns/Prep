
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class SagaPattern {

    interface SagaStep {

        void execute() throws Exception;

        void compensate();

        String name();
    }

    static class ReserveInventoryStep implements SagaStep {

        private final String item;
        private final int quantity;
        private boolean reserved;

        ReserveInventoryStep(String item, int quantity) {
            this.item = item;
            this.quantity = quantity;
        }

        @Override
        public void execute() throws Exception {
            System.out.println("Reserving " + quantity + " x " + item);
            reserved = true;
        }

        @Override
        public void compensate() {
            if (reserved) {
                System.out.println("Releasing reserved inventory for " + quantity + " x " + item);
                reserved = false;
            }
        }

        @Override
        public String name() {
            return "ReserveInventory";
        }
    }

    static class ChargePaymentStep implements SagaStep {

        private final String orderId;
        private final double amount;
        private boolean charged;

        ChargePaymentStep(String orderId, double amount) {
            this.orderId = orderId;
            this.amount = amount;
        }

        @Override
        public void execute() throws Exception {
            System.out.println("Charging payment " + amount + " for order " + orderId);
            charged = true;
        }

        @Override
        public void compensate() {
            if (charged) {
                System.out.println("Refunding payment " + amount + " for order " + orderId);
                charged = false;
            }
        }

        @Override
        public String name() {
            return "ChargePayment";
        }
    }

    static class ShipOrderStep implements SagaStep {

        private final String orderId;
        private boolean shipped;

        ShipOrderStep(String orderId) {
            this.orderId = orderId;
        }

        @Override
        public void execute() throws Exception {
            System.out.println("Shipping order " + orderId);
            shipped = true;
            throw new Exception("Shipment service unavailable");
        }

        @Override
        public void compensate() {
            if (shipped) {
                System.out.println("Reversing shipment for order " + orderId);
                shipped = false;
            } else {
                System.out.println("No shipment to reverse for order " + orderId);
            }
        }

        @Override
        public String name() {
            return "ShipOrder";
        }
    }

    static class Saga {

        private final List<SagaStep> steps = new ArrayList<>();
        private final Deque<SagaStep> completedSteps = new ArrayDeque<>();

        void addStep(SagaStep step) {
            steps.add(step);
        }

        void execute() {
            System.out.println("Starting saga execution...");
            try {
                for (SagaStep step : steps) {
                    System.out.println("Executing step: " + step.name());
                    step.execute();
                    completedSteps.push(step);
                }
                System.out.println("Saga completed successfully.");
            } catch (Exception e) {
                System.out.println("Saga failed: " + e.getMessage());
                compensate();
            }
        }

        private void compensate() {
            System.out.println("Starting compensation...");
            while (!completedSteps.isEmpty()) {
                SagaStep step = completedSteps.pop();
                System.out.println("Compensating step: " + step.name());
                step.compensate();
            }
            System.out.println("Saga compensation finished.");
        }
    }

    public static void main(String[] args) {
        Saga saga = new Saga();
        saga.addStep(new ReserveInventoryStep("Widget", 5));
        saga.addStep(new ChargePaymentStep("ORDER-123", 42.50));
        saga.addStep(new ShipOrderStep("ORDER-123"));

        saga.execute();
    }
}
