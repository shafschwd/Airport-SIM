package entities;

public class RefuelingTruck {
    private static final Object lock = new Object();

    public static void refuel(Plane plane) {
        synchronized (lock) {
            try {
                System.out.println("Refueling Truck: Refueling Plane-" + plane.getPlaneId());
                Thread.sleep(1500); // Simulate refueling time
                System.out.println("Refueling Truck: Completed refueling Plane-" + plane.getPlaneId());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}