package LLD.LiftSystem;

import LLD.LiftSystem.model.Direction;
import LLD.LiftSystem.model.HallReq;
import LLD.LiftSystem.repo.LiftRepository;
import LLD.LiftSystem.service.LiftSystemService;
import LLD.LiftSystem.strategy.ProximityStrategy;

public class Driver {

    public static void main(String[] args) throws InterruptedException {
        Driver app = new Driver();
        app.run();
    }

    void run() throws InterruptedException {
        runNormal();
    }

    private void runNormal() throws InterruptedException {
        System.out.println("Normal");

        LiftRepository liftRepository = new LiftRepository();
        LiftSystemService service = new LiftSystemService(liftRepository, new ProximityStrategy());

        service.initialize(5, 10);
        service.startController();

        service.submitHallReq(new HallReq(5, Direction.DOWN));
        service.submitHallReq(new HallReq(3, Direction.UP));
        service.submitHallReq(new HallReq(5, Direction.DOWN));

        Thread.sleep(1000);

        service.submitHallReq(new HallReq(1, Direction.DOWN));
        service.submitHallReq(new HallReq(5, Direction.UP));
        service.submitHallReq(new HallReq(3, Direction.UP));

        Thread.sleep(1000);

        service.submitHallReq(new HallReq(5, Direction.DOWN));
        service.submitHallReq(new HallReq(1, Direction.UP));

        Thread.sleep(5000);

        System.out.println("Shutting down");
        service.shutdown();
    }
}
