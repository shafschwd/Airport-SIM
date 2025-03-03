package main.entities;

import main.controllers.AirTrafficController;
import main.controllers.AirportManager;

public class Plane extends Thread {
    private final int planeId;
    private final AirTrafficController atc;
    private final AirportManager manager;
    private final boolean emergency;

    public Plane(int planeId, AirTrafficController atc, AirportManager manager, boolean emergency) {
        super("Plane-" + planeId);
        this.planeId = planeId;
        this.atc = atc;
        this.manager = manager;
        this.emergency = emergency;
    }

    public Plane(int planeId, AirTrafficController atc, AirportManager manager) {
        this(planeId, atc, manager, false);
    }

    public int getPlaneId() {
        return planeId;
    }

    public boolean isEmergency() {
        return emergency;
    }

    @Override
    public void run() {
        try {
            // Request landing and wait for clearance
            atc.requestLanding(this, manager);

            // Try to get a gate assignment
            int gateNumber = -1;
            while (gateNumber == -1) {
                try {
                    gateNumber = manager.assignGate(this);
                } catch (InterruptedException e) {
                    continue;
                }
            }

            System.out.println("🎤 P-" + planeId + ": 'Tower, taxiing to Gate " + (gateNumber + 1) + ".'");
            Thread.sleep(1000);

            // Execute concurrent passenger operations and aircraft servicing
            manager.performGateOperations(this, gateNumber);

            // Request refueling before takeoff
            RefuelingTruck.requestRefuel(this);

            // Wait with timeout to prevent indefinite blocking
            synchronized (this) {
                try {
                    wait(5000); // Wait up to 5 seconds for refueling
                } catch (InterruptedException e) {
                    // Continue even if interrupted
                }
            }

            // Release the gate before takeoff
            manager.releaseGate(this, gateNumber);

            // Request takeoff
            atc.requestTakeoff(this, manager);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}