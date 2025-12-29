package Java;

public class InitializationOrder {
    static {
        System.out.println("Static block 1");
    }

    {
        System.out.println("Instance initializer block");
    }

    public InitializationOrder() {
        System.out.println("Constructor");
    }

    static {
        System.out.println("Static block 2");
    }

    public static void main(String[] args) {
        System.out.println("Main method starts");
        new InitializationOrder();
        new InitializationOrder();
    }
}
