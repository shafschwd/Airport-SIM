package controllers;

import entities.Plane;
import java.util.LinkedList;
import java.util.Queue;

public class AirportManager {
    private int totalPlanesOnGround = 0;
    private int planesAtGates = 0;
    private int planesOnRunway = 0;
    private final boolean[] gates = new boolean[3];
    private final long[] gateStartTimes = new long[3];
    private final Queue<Plane> holdingQueue = new LinkedList<>();

    // Statistics tracking
    private int totalPlanesHandled = 0;
    private int successfulLandings = 0;
    private int successfulTakeoffs = 0;
    private long totalGateOccupancyTime = 0;

    public synchronized int getTotalPlanesOnGround() {
        return totalPlanesOnGround;
    }

    public synchronized void incrementGroundPlanes() {
        totalPlanesOnGround++;
        planesOnRunway++; // When a plane lands, it's initially on the runway
        successfulLandings++;
        totalPlanesHandled++;
        printGroundStatus();
    }

    public synchronized void decrementGroundPlanes() {
        if (totalPlanesOnGround > 0) {
            totalPlanesOnGround--;
            successfulTakeoffs++;
            printGroundStatus();
            notifyAll();
        }
    }

    public synchronized int assignGate(Plane plane) throws InterruptedException {
        // Check if this plane was already in the holding queue
        boolean wasInQueue = holdingQueue.remove(plane);

        while (true) {
            for (int i = 0; i < gates.length; i++) {
                if (!gates[i]) {
                    gates[i] = true;
                    planesAtGates++;

                    // Decrement runway count as plane moves from runway to gate
                    if (planesOnRunway > 0) {
                        planesOnRunway--;
                    }

                    gateStartTimes[i] = System.currentTimeMillis();
                    System.out.println("🛬 " + plane.getName() + " assigned to Gate " + (i + 1) + ".");
                    printGroundStatus();
                    return i;
                }
            }

            // If we get here, no gates are available
            if (!wasInQueue) {
                System.out.println("🚧 " + plane.getName() + " is in HOLDING (No available gates).");
                holdingQueue.add(plane);
            }

            wait(); // Wait for a gate to become available
        }
    }

    public synchronized void releaseGate(Plane plane, int gateNumber) {
        if (gateNumber >= 0 && gateNumber < gates.length && gates[gateNumber]) {
            gates[gateNumber] = false;
            planesAtGates--;

            // Increment runway count as plane moves from gate to runway for takeoff
            // But only if the runway isn't already occupied
            if (planesOnRunway == 0) {
                planesOnRunway = 1;
            }

            long occupancyTime = System.currentTimeMillis() - gateStartTimes[gateNumber];
            totalGateOccupancyTime += occupancyTime;

            System.out.println("🚪 " + plane.getName() + " has left Gate " + (gateNumber + 1) + ".");
            printGroundStatus();

            // Wake up all waiting threads
            notifyAll();
        }
    }

    public synchronized void planeTakeoff() {
        // Plane leaves the runway after takeoff
        if (planesOnRunway > 0) {
            planesOnRunway--;
        }
    }

    public synchronized boolean canPlaneLand() {
        // We can only accept landings if:
        // 1. We're below max capacity (3 planes total)
        // 2. The runway isn't already occupied
        return totalPlanesOnGround < 3 && planesOnRunway == 0;
    }

    public void printGroundStatus() {
        System.out.println("📊 Planes on ground: " + totalPlanesOnGround +
                " (Runway: " + planesOnRunway + ", Gates: " + planesAtGates + ")");
    }

    public void printFinalStatistics() {
        System.out.println("\n========= FINAL AIRPORT STATISTICS =========");
        System.out.println("Total planes handled: " + totalPlanesHandled);
        System.out.println("Total successful landings: " + successfulLandings);
        System.out.println("Total successful takeoffs: " + successfulTakeoffs);
        System.out.println("Total emergency landings: 0");
        System.out.println("Total planes diverted: 0");

        long avgOccupancyTime = (successfulLandings > 0) ?
                totalGateOccupancyTime / successfulLandings : 0;
        System.out.println("Average gate occupancy time: " + avgOccupancyTime + " ms");
        System.out.println("Planes remaining in holding: " + holdingQueue.size());
        System.out.println("===========================================");
    }

    public synchronized void performGateOperations(Plane plane, int gateNumber) throws InterruptedException {
        // Create two threads - one for passenger operations, one for aircraft servicing
        Thread passengerOps = new Thread(() -> {
            try {
                System.out.println("🧑‍✈️ " + plane.getName() + ": Passengers disembarking at Gate " + (gateNumber + 1));
                Thread.sleep(2000);
                System.out.println("🧑‍✈️ " + plane.getName() + ": New passengers boarding at Gate " + (gateNumber + 1));
                Thread.sleep(2000);
                System.out.println("✅ " + plane.getName() + ": Passenger operations completed at Gate " + (gateNumber + 1));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        Thread servicingOps = new Thread(() -> {
            try {
                System.out.println("🧹 " + plane.getName() + ": Cleaning in progress at Gate " + (gateNumber + 1));
                Thread.sleep(2000);
                System.out.println("📦 " + plane.getName() + ": Restocking supplies at Gate " + (gateNumber + 1));
                Thread.sleep(2000);
                System.out.println("✅ " + plane.getName() + ": Servicing completed at Gate " + (gateNumber + 1));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        // Start both operations concurrently
        passengerOps.start();
        servicingOps.start();

        // Wait for both operations to complete
        passengerOps.join();
        servicingOps.join();

        System.out.println("✅ " + plane.getName() + ": All gate operations completed at Gate " + (gateNumber + 1));
    }
}