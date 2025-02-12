package entities;

public class Passenger extends Thread {
    private final int passengerId;
    private final Plane plane;

    public Passenger(int passengerId, Plane plane) {
        this.passengerId = passengerId;
        this.plane = plane;
    }

    @Override
    public void run() {
        System.out.println("Passenger-" + passengerId + " is boarding " + plane.getName());
        try {
            Thread.sleep(500); // Simulate boarding time
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Passenger-" + passengerId + " has boarded " + plane.getName());
    }
}