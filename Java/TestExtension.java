package Java;

class A {
    A() {
        show();
    }

    void show() {
        System.out.println("A");
    }
}

class B extends A {
    int x = 1;

    void show() {
        System.out.println(x);
    }
}

class Animal {
    void sound() {
        System.out.println("Animal sound");
    }
}

class Dog extends Animal {
    void sound() {
        System.out.println("Bark");
    }
}

public class TestExtension {
    public static void main(String[] args) {
        new B();

        Animal a = new Dog(); // Reference type is Animal, object is Dog
        a.sound(); // Outputs: Bark (Dog's method is called)
    }
}
