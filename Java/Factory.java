package Java;

interface Transport {
    void deliver();
}

class Truck implements Transport {
    public void deliver() {
        System.out.println("Delivering by truck in a box.");
    }
}

abstract class Logistics {
    abstract Transport createTransport();

    public void deliver() {
        Transport t = createTransport();
        t.deliver();
    }
}

class RoadTransport extends Logistics {
    @Override
    public Transport createTransport() {
        return new Truck();
    }
}

public class Factory {

    public static void main(String[] ar) {
        Logistics l = new RoadTransport();
        l.deliver();
    }
}
