package LLD.RateLimiterApp.entity;

import java.util.UUID;

import LLD.RateLimiterApp.model.RateLimitAlgorithm;

public class RequestLogEntity {
    private final String requestId;
    private final String clientId;
    private final String resourcePath;
    private final RateLimitAlgorithm algorithm;
    private final boolean allowed;
    private final long requestTime;
    private final String reason;

    public RequestLogEntity(String clientId, String resourcePath, RateLimitAlgorithm algorithm,
            boolean allowed, long requestTime, String reason) {
        this.requestId = UUID.randomUUID().toString();
        this.clientId = clientId;
        this.resourcePath = resourcePath;
        this.algorithm = algorithm;
        this.allowed = allowed;
        this.requestTime = requestTime;
        this.reason = reason;
    }

    public String getRequestId() {
        return requestId;
    }

    public String getClientId() {
        return clientId;
    }

    public String getResourcePath() {
        return resourcePath;
    }

    public RateLimitAlgorithm getAlgorithm() {
        return algorithm;
    }

    public boolean isAllowed() {
        return allowed;
    }

    public long getRequestTime() {
        return requestTime;
    }

    public String getReason() {
        return reason;
    }

    @Override
    public String toString() {
        return "RequestLogEntity{requestId='" + requestId + "', clientId='" + clientId
                + "', resourcePath='" + resourcePath + "', algorithm=" + algorithm
                + ", allowed=" + allowed + ", requestTime=" + requestTime
                + ", reason='" + reason + "'}";
    }
}
