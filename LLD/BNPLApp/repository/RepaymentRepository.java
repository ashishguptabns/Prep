package LLD.BNPLApp.repository;

import LLD.BNPLApp.entity.RepaymentEntity;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RepaymentRepository {
    private final Map<String, RepaymentEntity> repayments = new ConcurrentHashMap<>();

    public void save(RepaymentEntity repayment) {
        repayments.put(repayment.getRepaymentId(), repayment);
    }

    public List<RepaymentEntity> findByUserId(String userId) {
        List<RepaymentEntity> result = new ArrayList<>();
        for (RepaymentEntity repayment : repayments.values()) {
            if (repayment.getUserId().equals(userId)) {
                result.add(repayment);
            }
        }
        return result;
    }
}
