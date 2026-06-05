package LLD.FlashSaleApp;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import LLD.FlashSaleApp.entity.AllocationEntity;
import LLD.FlashSaleApp.entity.ProductEntity;
import LLD.FlashSaleApp.entity.SaleEntity;
import LLD.FlashSaleApp.model.AllocationResult;
import LLD.FlashSaleApp.service.FlashSaleService;
import LLD.FlashSaleApp.service.FlashSaleService.SaleSummary;

public class Driver {

    public static void main(String[] args) throws InterruptedException {
        FlashSaleService service = new FlashSaleService();

        ProductEntity iPhone = service.createProduct("iPhone 15 Pro", 1_299);
        System.out.println("Product created: " + iPhone);
        System.out.println();

        long now = System.currentTimeMillis();
        long saleEndTime = now + 7_000;
        SaleEntity sale = service.startFlashSale(iPhone.getProductId(), 10, now, saleEndTime);
        System.out.println("Flash sale started: " + sale);
        System.out.println();

        int numUsers = 10;
        int quantityPerRequest = 3;
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch endLatch = new CountDownLatch(numUsers);

        ExecutorService executor = Executors.newFixedThreadPool(numUsers);

        long startTime = System.currentTimeMillis();

        for (int i = 1; i <= numUsers; i++) {
            final int userId = i;
            executor.submit(() -> {
                try {
                    startLatch.await();

                    String userIdStr = "user_" + userId;
                    AllocationResult result = service.attemptAllocation(sale.getSaleId(), userIdStr, quantityPerRequest);
                    System.out.println("[" + userIdStr + "] " + result);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    endLatch.countDown();
                }
            });
        }

        startLatch.countDown();
        endLatch.await();
        executor.shutdown();

        long duration = System.currentTimeMillis() - startTime;
        System.out.println("\nConcurrent allocation completed in " + duration + "ms");

        SaleSummary summary = service.getSaleSummary(sale.getSaleId());
        System.out.println("Sale Summary: " + summary);

        List<AllocationEntity> allocations = service.getSaleAllocations(sale.getSaleId());
        System.out.println("First 10 allocations:");
        for (int i = 0; i < Math.min(10, allocations.size()); i++) {
            System.out.println("  " + allocations.get(i));
        }

        System.out.println("User 'user_1' allocation history:");
        List<AllocationEntity> userHistory = service.getUserAllocationHistory("user_1");
        for (AllocationEntity a : userHistory) {
            System.out.println("  " + a);
        }

        System.out.println("Remaining inventory: "
                + service.getRemainingInventory(sale.getSaleId())
                + " units");
    }
}
