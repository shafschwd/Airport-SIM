package controllers;

import entities.Plane;
import java.util.LinkedList;
import java.util.Queue;

public class AirTrafficController {
    private boolean runwayAvailable = true;
    private final Queue<Plane> normalQueue = new LinkedList<>();

    public synchronized void requestLanding(Plane plane, AirportManager manager) throws InterruptedException {
        System.out.println("🎤 P-" + plane.getPlaneId() + ": 'Tower, this is " + plane.getName() + ", requesting permission to land.'");
        normalQueue.add(plane);

        while (true) {
            if (!runwayAvailable) {
                System.out.println("🎤 ATC: '" + plane.getName() + ", hold position. Runway is currently occupied.'");
                wait();
                continue;
            }

            if (!manager.canPlaneLand()) {
                System.out.println("🎤 ATC: '" + plane.getName() + ", hold position. All gates are full, cannot accept more arrivals.'");
                wait();
                continue;
            }

            break;
        }

        Plane landingPlane = normalQueue.poll();
        System.out.println("🎤 ATC: '" + landingPlane.getName() + ", cleared to land. Proceed to runway.'");

        runwayAvailable = false;
        Thread.sleep(2000);
        System.out.println("✅ " + landingPlane.getName() + " has LANDED.");
        manager.incrementGroundPlanes();
        runwayAvailable = true;
        notifyAll();
    }

    public synchronized void requestTakeoff(Plane plane, AirportManager manager) throws InterruptedException {
        System.out.println("🎤 P-" + plane.getPlaneId() + ": 'Tower, this is " + plane.getName() + ", requesting takeoff clearance.'");

        while (!runwayAvailable) {
            System.out.println("🎤 ATC: '" + plane.getName() + ", hold position. Runway occupied.'");
            wait();
        }

        runwayAvailable = false;
        System.out.println("🎤 ATC: '" + plane.getName() + ", cleared for takeoff. Safe flight!'");
        Thread.sleep(2000);
        System.out.println("✅ " + plane.getName() + " has successfully taken off.");
        manager.planeTakeoff(); // Call this BEFORE decrementGroundPlanes to ensure correct counting
        manager.decrementGroundPlanes();
        runwayAvailable = true;
        notifyAll();
    }
}