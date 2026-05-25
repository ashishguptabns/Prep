package LLD.BNPLApp.entity;

import java.util.UUID;

public class RepaymentEntity {
    private final String repaymentId;
    private final String userId;
    private final long amount;

    public RepaymentEntity(String userId, long amount) {
        this.repaymentId = UUID.randomUUID().toString();
        this.userId = userId;
        this.amount = amount;
    }

    public String getRepaymentId() {
        return repaymentId;
    }

    public String getUserId() {
        return userId;
    }

    public long getAmount() {
        return amount;
    }

    @Override
    public String toString() {
        return "RepaymentEntity{repaymentId='" + repaymentId + "', userId='" + userId
                + "', amount=" + amount + "}";
    }
}
