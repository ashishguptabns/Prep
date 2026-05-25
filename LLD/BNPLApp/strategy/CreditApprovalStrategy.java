package LLD.BNPLApp.strategy;

import LLD.BNPLApp.entity.UserEntity;

public interface CreditApprovalStrategy {
    boolean approve(UserEntity user, long amount);
}
