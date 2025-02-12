package controllers;

import java.util.concurrent.Semaphore;
import entities.Plane;

public class AirTrafficController {
    private final Semaphore runway = new Semaphore(1); // Only 1 plane on the runway

    public void requestLanding(Plane plane) throws InterruptedException {
        runway.acquire();
        System.out.println(Thread.currentThread().getName() + ": Plane " + plane.getPlaneId() + " is landing.");
        Thread.sleep(2000); // Simulate landing time
        System.out.println(Thread.currentThread().getName() + ": Plane " + plane.getPlaneId() + " has landed.");
        runway.release();
    }

    public void requestTakeoff(Plane plane) throws InterruptedException {
        runway.acquire();
        System.out.println(Thread.currentThread().getName() + ": Plane " + plane.getPlaneId() + " is taking off.");
        Thread.sleep(2000); // Simulate takeoff time
        System.out.println(Thread.currentThread().getName() + ": Plane " + plane.getPlaneId() + " has taken off.");
        runway.release();
    }
}