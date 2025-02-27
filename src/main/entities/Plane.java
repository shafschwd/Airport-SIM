package entities;

import controllers.AirTrafficController;
import controllers.AirportManager;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Plane extends Thread {
    private final int planeId;
    private final AirTrafficController atc;
    private final AirportManager manager;
    private final boolean lowFuel;

    private boolean checkBadWeather() {
        return new Random().nextInt(100) < 30; // 30% chance of bad weather
    }

    public Plane(int planeId, AirTrafficController atc, AirportManager manager) {
        super("Plane-" + planeId);
        this.planeId = planeId;
        this.atc = atc;
        this.manager = manager;
        this.lowFuel = new Random().nextInt(100) < 30; // % chance of low fuel
    }

    public int getPlaneId() {
        return planeId;
    }

    public boolean hasLowFuel() {
        return lowFuel;
    }

    public void boardPassengers() {
        ExecutorService executor = Executors.newFixedThreadPool(5); // 5 passengers at a time
        for (int i = 0; i < 50; i++) {
            executor.submit(new Passenger(i, this));
        }
        executor.shutdown();
        try {
            executor.awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {

            if (checkBadWeather()) {
                int delayTime = 3000 + new Random().nextInt(3000);
                System.out.println("\n🌧️  WEATHER ALERT! " + getName() + " takeoff delayed by " + delayTime + "ms.");
                System.out.println("🌧️  Waiting for clearance...");
                Thread.sleep(delayTime);
            }

            if (lowFuel) {
                System.out.println("🚨 " + getName() + " has LOW FUEL and requests an EMERGENCY LANDING!");
                atc.requestEmergencyLanding(this);
            } else {
                atc.requestLanding(this);
            }

            // Wait if gates are full
            while (manager.getAvailableGates() == 0) {
                System.out.println(getName() + " is holding in airspace.");
                Thread.sleep(2000 + new Random().nextInt(3000)); // Wait 2-5 seconds before retrying
            }

            System.out.println(getName() + " is taxiing to the gate.");
            Thread.sleep(1000); // Simulate taxiing time

            System.out.println(getName() + " has completed boarding and disembarking.");
            manager.recordPlaneService();
            manager.recordPassengerBoarding(50);

            RefuelingTruck.refuel(this);
            atc.requestTakeoff(this);
            manager.releaseGate(this);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}