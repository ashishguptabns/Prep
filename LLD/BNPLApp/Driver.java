package LLD.BNPLApp;

import LLD.BNPLApp.entity.ProductEntity;
import LLD.BNPLApp.entity.UserEntity;
import LLD.BNPLApp.model.AccountView;
import LLD.BNPLApp.service.BnplService;
import LLD.BNPLApp.strategy.SimpleCreditLimitStrategy;

public class Driver {

    public static void main(String[] args) {
        BnplService service = new BnplService(new SimpleCreditLimitStrategy());

        UserEntity user = service.createUser("Asha", 10_000);
        ProductEntity phone = service.createProduct("Phone", 7_500, 2);
        ProductEntity headphones = service.createProduct("Headphones", 2_000, 3);

        AccountView accountView = service.getAccountView(user.getUserId());
        System.out.println(accountView);
        System.out.println("Phone stock left: " + service.availableQuantity(phone.getProductId()));

        service.purchase(user.getUserId(), phone.getProductId(), 1);
        service.purchase(user.getUserId(), headphones.getProductId(), 1);

        System.out.println(service.getAccountView(user.getUserId()));

        service.repay(user.getUserId(), 3_000);

        System.out.println(service.getAccountView(user.getUserId()));
        System.out.println("Phone stock left: " + service.availableQuantity(phone.getProductId()));
    }
}
