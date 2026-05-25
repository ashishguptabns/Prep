package LLD.BNPLApp.model;

import LLD.BNPLApp.entity.UserEntity;

public class AccountView {
    private final UserEntity user;
    private final long totalPurchases;
    private final long totalRepayments;

    public AccountView(UserEntity user, long totalPurchases, long totalRepayments) {
        this.user = user;
        this.totalPurchases = totalPurchases;
        this.totalRepayments = totalRepayments;
    }

    public UserEntity getUser() {
        return user;
    }

    public long getTotalPurchases() {
        return totalPurchases;
    }

    public long getTotalRepayments() {
        return totalRepayments;
    }

    @Override
    public String toString() {
        return "AccountView{userId='" + user.getUserId() + "', name='" + user.getName()
                + "', creditLimit=" + user.getCreditLimit()
                + ", outstandingAmount=" + user.getOutstandingAmount()
                + ", availableCredit=" + user.getAvailableCredit()
                + ", totalPurchases=" + totalPurchases
                + ", totalRepayments=" + totalRepayments + "}";
    }
}
