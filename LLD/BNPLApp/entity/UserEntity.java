package LLD.BNPLApp.entity;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class UserEntity {
    private final String userId;
    private final String name;
    private final AtomicLong creditLimit;
    private final AtomicLong outstandingAmount;

    public UserEntity(String name, long creditLimit) {
        this.userId = UUID.randomUUID().toString();
        this.name = name;
        this.creditLimit = new AtomicLong(creditLimit);
        this.outstandingAmount = new AtomicLong(0);
    }

    public String getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public long getCreditLimit() {
        return creditLimit.get();
    }

    public long getOutstandingAmount() {
        return outstandingAmount.get();
    }

    public long getAvailableCredit() {
        return getCreditLimit() - getOutstandingAmount();
    }

    public boolean assignCreditLimit(long newCreditLimit) {
        if (newCreditLimit < outstandingAmount.get()) {
            return false;
        }
        creditLimit.set(newCreditLimit);
        return true;
    }

    public boolean addOutstandingIfWithinLimit(long amount) {
        while (true) {
            long currentOutstanding = outstandingAmount.get();
            long nextOutstanding = currentOutstanding + amount;
            if (nextOutstanding > creditLimit.get()) {
                return false;
            }
            if (outstandingAmount.compareAndSet(currentOutstanding, nextOutstanding)) {
                return true;
            }
        }
    }

    public long repay(long amount) {
        while (true) {
            long currentOutstanding = outstandingAmount.get();
            long appliedAmount = Math.min(currentOutstanding, amount);
            long nextOutstanding = currentOutstanding - appliedAmount;
            if (outstandingAmount.compareAndSet(currentOutstanding, nextOutstanding)) {
                return appliedAmount;
            }
        }
    }

    @Override
    public String toString() {
        return "UserEntity{userId='" + userId + "', name='" + name + "', creditLimit="
                + getCreditLimit() + ", outstandingAmount=" + getOutstandingAmount() + "}";
    }
}
