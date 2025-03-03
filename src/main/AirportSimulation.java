package main;

import main.controllers.AirTrafficController;
import main.controllers.AirportManager;
import main.entities.Plane;
import main.entities.RefuelingTruck;
import java.util.Random;

public class AirportSimulation {
    public static void main(String[] args) {
        AirTrafficController atc = new AirTrafficController();
        AirportManager manager = new AirportManager();
        Random rand = new Random();

        // Track the start time
        long startTime = System.currentTimeMillis();

        // Start the refueling truck service as a daemon thread
        Thread refuelingThread = new Thread(() -> {
            RefuelingTruck.startRefueling(atc, manager);
        });
        refuelingThread.setDaemon(true);
        refuelingThread.start();

        int totalPlanes = 6;
        Plane[] planes = new Plane[totalPlanes];

        // Create all planes first - make one plane an emergency aircraft
        for (int i = 0; i < totalPlanes; i++) {
            boolean isEmergency = rand.nextDouble() < 0.30; // 30% chance of emergency
            planes[i] = new Plane(i + 1, atc, manager, isEmergency);
        }

        // Start planes with staggered arrival times
        for (int i = 0; i < totalPlanes; i++) {
            planes[i].start();

            try {
                int delay = rand.nextInt(300) + 1000;
                System.out.println("⏳ Next plane arriving in " + (delay / 1000) + " seconds...");
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // Wait for all planes to complete
        for (Plane plane : planes) {
            try {
                plane.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // Print statistics and end
        manager.printFinalStatistics();

        long endTime = System.currentTimeMillis();
        double executionTimeSeconds = (endTime - startTime) / 1000.0;
        System.out.printf("\n⏱️ Total simulation time: %.2f seconds\n", executionTimeSeconds);

        System.exit(0);
    }
}