package LLD.BNPLApp;

import LLD.BNPLApp.entity.ProductEntity;
import LLD.BNPLApp.entity.PurchaseEntity;
import LLD.BNPLApp.entity.UserEntity;
import LLD.BNPLApp.inventory.InMemoryProductInventory;
import LLD.BNPLApp.model.AccountView;
import LLD.BNPLApp.repository.ProductRepository;
import LLD.BNPLApp.repository.PurchaseRepository;
import LLD.BNPLApp.repository.RepaymentRepository;
import LLD.BNPLApp.repository.UserRepository;
import LLD.BNPLApp.service.BnplService;

public class Driver {

    public static void main(String[] args) {
        BnplService service = new BnplService(
                new UserRepository(),
                new ProductRepository(),
                new PurchaseRepository(),
                new RepaymentRepository(),
                new InMemoryProductInventory());

        UserEntity user = service.createUser("Asha", 10_000);
        ProductEntity phone = service.createProduct("Phone", 7_500, 2);
        ProductEntity headphones = service.createProduct("Headphones", 2_000, 3);

        PurchaseEntity phonePurchase = service.purchase(user.getUserId(), phone.getProductId(), 1);
        PurchaseEntity headphonePurchase = service.purchase(user.getUserId(), headphones.getProductId(), 1);

        System.out.println(phonePurchase);
        System.out.println(headphonePurchase);
        System.out.println(service.getAccountView(user.getUserId()));

        service.repay(user.getUserId(), 3_000);

        AccountView accountView = service.getAccountView(user.getUserId());
        System.out.println(accountView);
        System.out.println("Phone stock left: " + service.availableQuantity(phone.getProductId()));
    }
}
