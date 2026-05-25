package LLD.BNPLApp.strategy;

import LLD.BNPLApp.entity.UserEntity;

public class SimpleCreditLimitStrategy implements CreditApprovalStrategy {
    @Override
    public boolean approve(UserEntity user, long amount) {
        return user.getAvailableCredit() >= amount;
    }
}
