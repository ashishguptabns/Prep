package LLD.RateLimiterApp.model;

public class Request {

    private final String key;
    private final int cost;

    public Request(String key) {
        this(key, 1);
    }

    public Request(String key, int cost) {
        this.key = key;
        this.cost = cost;
    }

    public String getKey() {
        return key;
    }

    public int getCost() {
        return cost;
    }
}
