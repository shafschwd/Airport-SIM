package main.controllers;

import main.entities.Plane;
import java.util.LinkedList;
import java.util.Queue;

public class AirTrafficController {
    private boolean runwayAvailable = true;
    private final Queue<Plane> normalQueue = new LinkedList<>();
    private final Queue<Plane> emergencyQueue = new LinkedList<>();

    public synchronized void requestLanding(Plane plane, AirportManager manager) throws InterruptedException {
        if (plane.isEmergency()) {
            System.out.println("🎤 P-" + plane.getPlaneId() + ": '🚨 Tower, this is " + plane.getName() +
                    ", declaring EMERGENCY! Requesting immediate landing clearance.'");
            emergencyQueue.add(plane);
        } else {
            System.out.println("🎤 P-" + plane.getPlaneId() + ": 'Tower, this is " + plane.getName() +
                    ", requesting permission to land.'");
            normalQueue.add(plane);
        }

        while (true) {
            if (!runwayAvailable) {
                if (plane.isEmergency()) {
                    System.out.println("🎤 ATC: '🚨 EMERGENCY AIRCRAFT " + plane.getName() +
                            ", stand by, clearing runway for your arrival.'");
                } else {
                    System.out.println("🎤 ATC: '" + plane.getName() + ", hold position. Runway is currently occupied.'");
                }
                wait();
                continue;
            }

            if (!manager.canPlaneLand()) {
                if (plane.isEmergency()) {
                    System.out.println("🎤 ATC: '🚨 " + plane.getName() +
                            ", EMERGENCY ACKNOWLEDGED. Hold position. All gates are full, cannot accept more arrivals.'");
                } else {
                    System.out.println("🎤 ATC: '" + plane.getName() +
                            ", hold position. All gates are full, cannot accept more arrivals.'");
                }
                wait();
                continue;
            }

            // Check if this plane is next in queue (emergency queue gets priority)
            Plane nextPlane = null;
            if (!emergencyQueue.isEmpty()) {
                nextPlane = emergencyQueue.peek();
            } else if (!normalQueue.isEmpty()) {
                nextPlane = normalQueue.peek();
            }

            if (nextPlane != plane) {
                wait();
                continue;
            }

            break;
        }

        // Remove plane from its queue
        if (plane.isEmergency()) {
            emergencyQueue.poll();
        } else {
            normalQueue.poll();
        }

        if (plane.isEmergency()) {
            System.out.println("🎤 ATC: '" + plane.getName() + ", EMERGENCY landing cleared. Proceed to runway immediately.'");
        } else {
            System.out.println("🎤 ATC: '" + plane.getName() + ", cleared to land. Proceed to runway.'");
        }

        // Use manager's synchronization to update state atomically
        manager.acquireRunway();
        try {
            runwayAvailable = false;
            Thread.sleep(plane.isEmergency() ? 1000 : 2000); // Emergency planes land faster

            if (plane.isEmergency()) {
                System.out.println("✅ " + plane.getName() + " has EMERGENCY LANDED! 🚨");
                manager.recordEmergencyLanding();
            } else {
                System.out.println("✅ " + plane.getName() + " has LANDED.");
            }

            manager.incrementGroundPlanes();
        } finally {
            manager.releaseRunway();
            runwayAvailable = true;
            notifyAll();
        }
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

        // Use manager's synchronization to update state atomically
        manager.acquireRunway();
        try {
            System.out.println("✅ " + plane.getName() + " has successfully taken off.");
            manager.planeTakeoff();
            manager.decrementGroundPlanes();
        } finally {
            manager.releaseRunway();
            runwayAvailable = true;
            notifyAll();
        }
    }
}