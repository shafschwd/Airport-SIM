import controllers.AirTrafficController;
import controllers.AirportManager;
import entities.Plane;

import java.util.Random;

public class AirportSimulation {
    public static void main(String[] args) {
        AirTrafficController atc = new AirTrafficController();
        AirportManager manager = new AirportManager();
        Random rand = new Random();

        Plane[] planes = new Plane[6];

        // Create and start 6 planes with random arrival times
        for (int i = 0; i < 6; i++) {
            planes[i] = new Plane(i + 1, atc, manager);
            planes[i].start();
            try {
                Thread.sleep(rand.nextInt(2000)); // Random arrival between 0-2 seconds
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // Ensure all planes have completed their operations before printing final statistics
        for (Plane plane : planes) {
            try {
                plane.join(); // Wait for each plane to finish before proceeding
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // Run final statistics after all planes have completed their operations
        manager.printFinalStatistics();
    }
}