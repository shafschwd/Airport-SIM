package controllers;

import java.util.concurrent.Semaphore;
import utils.StatisticsCollector;
import entities.Plane;

public class AirportManager {
    private final Semaphore groundCapacity = new Semaphore(2); // Max 2 planes at the gates
    private int planesServed = 0;
    private int passengersBoarded = 0;

    public void requestGate(Plane plane) throws InterruptedException {
        if (groundCapacity.availablePermits() == 0) {
            System.out.println(plane.getName() + " is waiting for a gate to be available.");
        }
        groundCapacity.acquire(); // Blocks if no gates are available
        System.out.println(plane.getName() + " has arrived at a gate.");
    }

    public void releaseGate(Plane plane) {
        groundCapacity.release();
        System.out.println(plane.getName() + " has left the gate.");
    }

    public synchronized void recordPlaneService() {
        planesServed++;
    }

    public synchronized void recordPassengerBoarding(int count) {
        passengersBoarded += count;
    }

    public int getAvailableGates() {
        return groundCapacity.availablePermits();
    }

    public void printFinalStatistics() {
        System.out.println("--- Airport Final Statistics ---");
        System.out.println("Total Planes Served: " + planesServed);
        System.out.println("Total Passengers Boarded: " + passengersBoarded);
        StatisticsCollector.printStatistics();
    }
}