package LLD.BNPLApp.service;

import java.util.List;

import LLD.BNPLApp.entity.ProductEntity;
import LLD.BNPLApp.entity.PurchaseEntity;
import LLD.BNPLApp.entity.RepaymentEntity;
import LLD.BNPLApp.entity.UserEntity;
import LLD.BNPLApp.exception.BnplException;
import LLD.BNPLApp.inventory.InMemoryProductInventory;
import LLD.BNPLApp.inventory.ProductInventory;
import LLD.BNPLApp.model.AccountView;
import LLD.BNPLApp.model.PurchaseStatus;
import LLD.BNPLApp.repository.ProductRepository;
import LLD.BNPLApp.repository.PurchaseRepository;
import LLD.BNPLApp.repository.RepaymentRepository;
import LLD.BNPLApp.repository.UserRepository;
import LLD.BNPLApp.saga.PurchaseSaga;
import LLD.BNPLApp.strategy.CreditApprovalStrategy;

public class BnplService {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final PurchaseRepository purchaseRepository;
    private final RepaymentRepository repaymentRepository;
    private final ProductInventory productInventory;
    private final CreditApprovalStrategy creditApprovalStrategy;

    public BnplService(CreditApprovalStrategy creditApprovalStrategy) {
        this.userRepository = new UserRepository();
        this.productRepository = new ProductRepository();
        this.purchaseRepository = new PurchaseRepository();
        this.repaymentRepository = new RepaymentRepository();
        this.productInventory = new InMemoryProductInventory();
        this.creditApprovalStrategy = creditApprovalStrategy;
    }

    public UserEntity createUser(String name, long creditLimit) {
        if (name == null || name.isBlank()) {
            throw new BnplException("User name is required");
        }
        if (creditLimit < 0) {
            throw new BnplException("Credit limit cannot be negative");
        }
        UserEntity user = new UserEntity(name, creditLimit);
        userRepository.save(user);
        return user;
    }

    public void assignCreditLimit(String userId, long creditLimit) {
        if (creditLimit < 0) {
            throw new BnplException("Credit limit cannot be negative");
        }
        UserEntity user = findUser(userId);
        if (!user.assignCreditLimit(creditLimit)) {
            throw new BnplException("Credit limit cannot be lower than outstanding amount");
        }
    }

    public ProductEntity createProduct(String name, long price, int quantity) {
        if (name == null || name.isBlank()) {
            throw new BnplException("Product name is required");
        }
        if (price <= 0) {
            throw new BnplException("Product price must be positive");
        }
        if (quantity < 0) {
            throw new BnplException("Product quantity cannot be negative");
        }
        ProductEntity product = new ProductEntity(name, price);
        productRepository.save(product);
        productInventory.registerProduct(product.getProductId(), quantity);
        return product;
    }

    public PurchaseEntity purchase(String userId, String productId, int quantity) {
        if (quantity <= 0) {
            throw new BnplException("Purchase quantity must be positive");
        }
        UserEntity user = findUser(userId);
        ProductEntity product = findProduct(productId);
        long amount = product.getPrice() * quantity;
        PurchaseSaga saga = new PurchaseSaga();

        try {
            if (!creditApprovalStrategy.approve(user, amount)) {
                throw new BnplException("Insufficient credit for user: " + userId);
            }

            if (!productInventory.tryReserve(productId, quantity)) {
                throw new BnplException("Insufficient inventory for product: " + productId);
            }
            saga.addCompensation(() -> productInventory.release(productId, quantity));

            if (!user.addOutstandingIfWithinLimit(amount)) {
                throw new BnplException("Insufficient credit for user: " + userId);
            }
            saga.addCompensation(() -> user.repay(amount));

            PurchaseEntity purchase = new PurchaseEntity(userId, productId, quantity, amount, PurchaseStatus.APPROVED);
            purchaseRepository.save(purchase);
            saga.addCompensation(() -> purchaseRepository.delete(purchase.getPurchaseId()));

            saga.complete();
            return purchase;
        } catch (Exception exception) {
            saga.compensate();
            throw exception;
        }
    }

    public RepaymentEntity repay(String userId, long amount) {
        if (amount <= 0) {
            throw new BnplException("Repayment amount must be positive");
        }
        UserEntity user = findUser(userId);
        long appliedAmount = user.repay(amount);
        if (appliedAmount == 0) {
            throw new BnplException("No outstanding amount for user: " + userId);
        }

        RepaymentEntity repayment = new RepaymentEntity(userId, appliedAmount);
        repaymentRepository.save(repayment);
        return repayment;
    }

    public int availableQuantity(String productId) {
        findProduct(productId);
        return productInventory.availableQuantity(productId);
    }

    public AccountView getAccountView(String userId) {
        UserEntity user = findUser(userId);
        List<PurchaseEntity> purchases = purchaseRepository.findByUserId(userId);
        List<RepaymentEntity> repayments = repaymentRepository.findByUserId(userId);

        long totalPurchases = 0;
        for (PurchaseEntity purchase : purchases) {
            totalPurchases += purchase.getAmount();
        }

        long totalRepayments = 0;
        for (RepaymentEntity repayment : repayments) {
            totalRepayments += repayment.getAmount();
        }

        return new AccountView(user, totalPurchases, totalRepayments);
    }

    private UserEntity findUser(String userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new BnplException("User not found: " + userId));
    }

    private ProductEntity findProduct(String productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new BnplException("Product not found: " + productId));
    }
}
