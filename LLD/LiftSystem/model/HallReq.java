package LLD.LiftSystem.model;

public class HallReq extends Req {

    public HallReq(int fromFloor, Direction dir) {
        this.fromFloor = fromFloor;
        this.dir = dir;
    }

    public HallReq(int fromFloor, int toFloor) {
        this.fromFloor = fromFloor;
        this.toFloor = toFloor;
    }

    @Override
    public String toString() {
        return "Req - " + fromFloor + " Dir - " + dir.toString();
    }
}
