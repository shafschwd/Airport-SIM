public class RefuelingTruck {
    private static final Object lock = new Object();

    public static void refuel(Plane plane) {
        synchronized (lock) {
            try {
                System.out.println("Refueling Truck: Refueling " + plane.getName());
                Thread.sleep(1500); // Simulate refueling time
                System.out.println("Refueling Truck: Completed refueling " + plane.getName());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}