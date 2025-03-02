package controllers;

import entities.Plane;
import entities.Passenger;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Condition;

public class AirportManager {
    private int totalPlanesOnGround = 0;
    private int planesAtGates = 0;
    private int planesOnRunway = 0;
    private final boolean[] gates = new boolean[3];
    private final long[] gateStartTimes = new long[3];
    private final Queue<Plane> holdingQueue = new LinkedList<>();

    // Locks and conditions for better concurrency management
    private final ReentrantLock managerLock = new ReentrantLock();
    private final Condition gateAvailable = managerLock.newCondition();
    private final Semaphore runwaySemaphore = new Semaphore(1, true);

    // Statistics tracking
    private int totalPlanesHandled = 0;
    private int successfulLandings = 0;
    private int successfulTakeoffs = 0;
    private int emergencyLandings = 0;
    private long totalGateOccupancyTime = 0;
    private int totalPassengersEmbarked = 0;
    private int totalPassengersDisembarked = 0;

    public int getTotalPlanesOnGround() {
        managerLock.lock();
        try {
            return planesOnRunway + planesAtGates;
        } finally {
            managerLock.unlock();
        }
    }

    // Helper method to explicitly set runway status
    private void setRunwayStatus(int count) {
        managerLock.lock();
        try {
            planesOnRunway = count;
        } finally {
            managerLock.unlock();
        }
    }

    public void incrementGroundPlanes() {
        managerLock.lock();
        try {
            planesOnRunway = 1;
            successfulLandings++;
            totalPlanesHandled++;
            totalPlanesOnGround = planesOnRunway + planesAtGates;
            printGroundStatus();
        } finally {
            managerLock.unlock();
        }
    }

    public void decrementGroundPlanes() {
        managerLock.lock();
        try {
            planesOnRunway = 0;
            successfulTakeoffs++;
            totalPlanesOnGround = planesOnRunway + planesAtGates;
            printGroundStatus();
            gateAvailable.signalAll();
        } finally {
            managerLock.unlock();
        }
    }

    public void recordEmergencyLanding() {
        managerLock.lock();
        try {
            emergencyLandings++;
        } finally {
            managerLock.unlock();
        }
    }

    public void recordPassengerActivity(int embarked, int disembarked) {
        managerLock.lock();
        try {
            totalPassengersEmbarked += embarked;
            totalPassengersDisembarked += disembarked;
        } finally {
            managerLock.unlock();
        }
    }

    public int assignGate(Plane plane) throws InterruptedException {
        managerLock.lock();
        try {
            // Check if this plane was already in the holding queue
            boolean wasInQueue = holdingQueue.remove(plane);

            while (true) {
                for (int i = 0; i < gates.length; i++) {
                    if (!gates[i]) {
                        gates[i] = true;
                        planesAtGates++;
                        planesOnRunway = 0;
                        totalPlanesOnGround = planesOnRunway + planesAtGates;

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

                gateAvailable.await(); // Wait for a gate to become available
            }
        } finally {
            managerLock.unlock();
        }
    }

    public void releaseGate(Plane plane, int gateNumber) {
        managerLock.lock();
        try {
            if (gateNumber >= 0 && gateNumber < gates.length && gates[gateNumber]) {
                gates[gateNumber] = false;
                planesAtGates--;
                planesOnRunway = 1;
                totalPlanesOnGround = planesOnRunway + planesAtGates;

                // Record gate occupancy time
                long occupancyTime = System.currentTimeMillis() - gateStartTimes[gateNumber];
                totalGateOccupancyTime += occupancyTime;

                // Print the departure message after all state has been updated
                System.out.println("🚪 " + plane.getName() + " has left Gate " + (gateNumber + 1) + ".");

                // Print updated ground status only once after all changes are complete
                printGroundStatus();

                // Signal waiting threads
                gateAvailable.signalAll();
            }
        } finally {
            managerLock.unlock();
        }
    }

    public void acquireRunway() throws InterruptedException {
        runwaySemaphore.acquire();
    }

    public void releaseRunway() {
        runwaySemaphore.release();
    }

    public void planeTakeoff() {
        managerLock.lock();
        try {
            planesOnRunway = 0;
            totalPlanesOnGround = planesOnRunway + planesAtGates;
        } finally {
            managerLock.unlock();
        }
    }

    public boolean canPlaneLand() {
        managerLock.lock();
        try {
            // We can only accept landings if:
            // 1. We have fewer than 3 planes total on ground
            // 2. The runway isn't already occupied
            return (planesAtGates + planesOnRunway) < 3 && planesOnRunway == 0;
        } finally {
            managerLock.unlock();
        }
    }

    public void printGroundStatus() {
        System.out.println("📊 Planes on ground: " + getTotalPlanesOnGround() +
                " (Runway: " + planesOnRunway + ", Gates: " + planesAtGates + ")");
    }

    public void printFinalStatistics() {
        managerLock.lock();
        try {
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
        } finally {
            managerLock.unlock();
        }
    }

    public void performGateOperations(Plane plane, int gateNumber) throws InterruptedException {
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