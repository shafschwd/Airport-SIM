package controllers;

import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.Semaphore;
import entities.Plane;

public class AirTrafficController {
    private final Semaphore runway = new Semaphore(1);
    private final PriorityBlockingQueue<Plane> emergencyQueue = new PriorityBlockingQueue<>(10, (p1, p2) -> Boolean.compare(p2.hasLowFuel(), p1.hasLowFuel()));

    public void requestEmergencyLanding(Plane plane) throws InterruptedException {
        emergencyQueue.add(plane);
        System.out.println("🚨 Emergency landing request added for " + plane.getName());

        while (!emergencyQueue.peek().equals(plane) || runway.availablePermits() == 0) {
            Thread.sleep(500); // Wait for clearance
        }

        emergencyQueue.poll(); // Remove plane from emergency queue
        runway.acquire();
        System.out.println("🚨 " + plane.getName() + " is performing an emergency landing!");
        Thread.sleep(2000);
        System.out.println("✅ " + plane.getName() + " has safely landed.");
        runway.release();
    }

    public void requestLanding(Plane plane) throws InterruptedException {
        synchronized (this) {
            if (emergencyQueue.contains(plane)) return; // Avoid duplicate landing logs
        }
        runway.acquire();
        System.out.println(plane.getName() + " is landing.");
        Thread.sleep(2000);
        System.out.println(plane.getName() + " has landed.");
        runway.release();
    }


    public void requestTakeoff(Plane plane) throws InterruptedException {
        runway.acquire();
        System.out.println("\n---------------------------------");
        System.out.println("✈️  " + plane.getName() + " is taking off.");
        System.out.println("---------------------------------\n");
        Thread.sleep(2000);
        System.out.println("✅ " + plane.getName() + " has successfully taken off.");
        runway.release();
    }


}