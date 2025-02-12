package entities;

import controllers.AirTrafficController;
import controllers.AirportManager;

public class Plane extends Thread {
    private final int planeId; // Renamed from id to planeId
    private final AirTrafficController atc;
    private final AirportManager manager;

    public Plane(int planeId, AirTrafficController atc, AirportManager manager) {
        super("Plane-" + planeId);
        this.planeId = planeId;
        this.atc = atc;
        this.manager = manager;
    }

    public int getPlaneId() { // Renamed from getId() to getPlaneId()
        return planeId;
    }

    @Override
    public void run() {
        try {
            atc.requestLanding(this);
            System.out.println(getName() + " is taxiing to the gate.");

            Thread.sleep(1000); // Simulate gate operations
            System.out.println(getName() + " has completed boarding and disembarking.");

            manager.recordPlaneService();
            manager.recordPassengerBoarding(50); // Assume full capacity for simplicity

            atc.requestTakeoff(this);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}