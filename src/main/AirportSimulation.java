package main;

import controllers.AirTrafficController;
import controllers.AirportManager;
import entities.Plane;
import entities.RefuelingTruck;
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
        refuelingThread.setDaemon(true); // Makes the thread exit when all non-daemon threads are done
        refuelingThread.start();

        int totalPlanes = 6;
        Plane[] planes = new Plane[totalPlanes];

        // Create all planes first
        for (int i = 0; i < totalPlanes; i++) {
            planes[i] = new Plane(i + 1, atc, manager);
        }

        // Start planes with staggered but overlapping arrival times
        for (int i = 0; i < totalPlanes; i++) {
            planes[i].start();

            // Use shorter delays between plane starts to create more concurrent activity
            try {
                int delay = rand.nextInt(300) + 2000; // 2-5 seconds between plane starts
                System.out.println("⏳ Next plane arriving in " + (delay / 1000) + " seconds...");
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // Ensure all planes have completed their operations before printing final statistics
        for (Plane plane : planes) {
            try {
                plane.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // Print final statistics
        manager.printFinalStatistics();

        // Calculate and print execution time
        long endTime = System.currentTimeMillis();
        double executionTimeSeconds = (endTime - startTime) / 1000.0;
        System.out.printf("\n⏱️ Total simulation time: %.2f seconds\n", executionTimeSeconds);

        // Ensure the program terminates even if there are non-daemon threads
        System.exit(0);
    }
}