import controllers.AirTrafficController;
import entities.Plane;

import java.util.Random;

public class AirportSimulation {
    public static void main(String[] args) {
        AirTrafficController atc = new AirTrafficController();
        Random rand = new Random();

        // Create 6 planes with random arrival times
        for (int i = 1; i <= 6; i++) {
            Plane plane = new Plane(i, atc);
            plane.start();
            try {
                Thread.sleep(rand.nextInt(2000)); // Random arrival between 0-2 seconds
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}