package LLD.RateLimiterApp.model;

public class RequestLogEntity {

    private final String clientId;
    private final String resourcePath;
    private final long timestamp;

    public RequestLogEntity(String clientId, String resourcePath, long timestamp) {
        this.clientId = clientId;
        this.resourcePath = resourcePath;
        this.timestamp = timestamp;
    }

    public String getClientId() {
        return clientId;
    }

    public String getResourcePath() {
        return resourcePath;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
