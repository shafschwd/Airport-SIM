package controllers;

import entities.Plane;
import entities.Passenger;
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
    private int emergencyLandings = 0;
    private long totalGateOccupancyTime = 0;
    private int totalPassengersEmbarked = 0;
    private int totalPassengersDisembarked = 0;

    public synchronized int getTotalPlanesOnGround() {
        return totalPlanesOnGround;
    }

    // Helper method to explicitly set runway status
    private synchronized void setRunwayStatus(int count) {
        planesOnRunway = count;
    }

    public synchronized void incrementGroundPlanes() {
        totalPlanesOnGround++;
        setRunwayStatus(1); // Set runway status to exactly 1 plane
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

    public synchronized void recordEmergencyLanding() {
        emergencyLandings++;
    }

    public synchronized void recordPassengerActivity(int embarked, int disembarked) {
        totalPassengersEmbarked += embarked;
        totalPassengersDisembarked += disembarked;
    }

    public synchronized int assignGate(Plane plane) throws InterruptedException {
        // Check if this plane was already in the holding queue
        boolean wasInQueue = holdingQueue.remove(plane);

        while (true) {
            for (int i = 0; i < gates.length; i++) {
                if (!gates[i]) {
                    gates[i] = true;
                    planesAtGates++;

                    // Set runway count to exactly 0 as plane moves to gate
                    setRunwayStatus(0);

                    gateStartTimes[i] = System.currentTimeMillis();
                    System.out.println("🛬 " + plane.getName() + " assigned to Gate " + (i + 1) + ".");
                    printGroundStatus();
                    return i;
                }
            }

            // If we get here, no gates are available
            if (!wasInQueue) {
                System.out.println("🚧 " + plane.getName() +
                        (plane.isEmergency() ? " 🚨 (EMERGENCY) " : " ") +
                        "is in HOLDING (No available gates).");
                holdingQueue.add(plane);
            }

            wait(); // Wait for a gate to become available
        }
    }

    public synchronized void releaseGate(Plane plane, int gateNumber) {
        if (gateNumber >= 0 && gateNumber < gates.length && gates[gateNumber]) {
            gates[gateNumber] = false;
            planesAtGates--;

            // Set runway count to exactly 1 as plane moves to runway for takeoff
            setRunwayStatus(1);

            long occupancyTime = System.currentTimeMillis() - gateStartTimes[gateNumber];
            totalGateOccupancyTime += occupancyTime;

            System.out.println("🚪 " + plane.getName() + " has left Gate " + (gateNumber + 1) + ".");
            printGroundStatus();

            // Wake up all waiting threads
            notifyAll();
        }
    }

    public synchronized void planeTakeoff() {
        // Set runway count to exactly 0 after takeoff
        setRunwayStatus(0);
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
        System.out.println("Total emergency landings: " + emergencyLandings);
        System.out.println("Total successful takeoffs: " + successfulTakeoffs);
        System.out.println("Total passengers embarked: " + totalPassengersEmbarked);
        System.out.println("Total passengers disembarked: " + totalPassengersDisembarked);

        long avgOccupancyTime = (successfulLandings > 0) ?
                totalGateOccupancyTime / successfulLandings : 0;
        System.out.println("Average gate occupancy time: " + avgOccupancyTime + " ms");
        System.out.println("Planes remaining in holding: " + holdingQueue.size());
        System.out.println("===========================================");
    }

    public synchronized void performGateOperations(Plane plane, int gateNumber) throws InterruptedException {
        // Generate random passenger counts (1-50)
        int disembarkingCount = (int)(Math.random() * 50) + 1;
        int boardingCount = (int)(Math.random() * 50) + 1;

        // Record the counts
        recordPassengerActivity(boardingCount, disembarkingCount);

        // Generate passenger objects
        Passenger[] disembarking = Passenger.generatePassengers(plane, true, disembarkingCount);
        Passenger[] boarding = Passenger.generatePassengers(plane, false, boardingCount);

        // Create two threads - one for passenger operations, one for aircraft servicing
        Thread passengerOps = new Thread(() -> {
            try {
                System.out.println("🧑‍✈️ " + plane.getName() + ": " + disembarkingCount +
                        " passengers disembarking at Gate " + (gateNumber + 1));
                Thread.sleep(2000);
                System.out.println("🧑‍✈️ " + plane.getName() + ": " + boardingCount +
                        " passengers boarding at Gate " + (gateNumber + 1));
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