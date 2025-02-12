package entities;

import controllers.AirTrafficController;

public class Plane extends Thread {
    private final int id;
    private final AirTrafficController atc;

    public Plane(int id, AirTrafficController atc) {
        super("Plane-" + id);
        this.id = id;
        this.atc = atc;
    }

    public int getId() {
        return id;
    }

    @Override
    public void run() {
        try {
            atc.requestLanding(this);
            System.out.println(getName() + " is taxiing to the gate.");

            Thread.sleep(1000); // Simulate gate operations
            System.out.println(getName() + " has completed boarding and disembarking.");

            atc.requestTakeoff(this);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}