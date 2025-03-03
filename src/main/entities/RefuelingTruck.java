package main.entities;

import main.controllers.AirTrafficController;
import main.controllers.AirportManager;
import java.util.LinkedList;
import java.util.Queue;

public class RefuelingTruck {
    private static final Queue<Plane> refuelQueue = new LinkedList<>();
    private static final Object lock = new Object();

    public static void requestRefuel(Plane plane) {
        synchronized (lock) {
            System.out.println("⛽ " + plane.getName() + " is added to the refueling queue.");
            refuelQueue.add(plane);
            lock.notify();
        }
    }

    public static void startRefueling(AirTrafficController atc, AirportManager manager) {
        new Thread(() -> {
            while (true) {
                Plane planeToRefuel = null;
                synchronized (lock) {
                    try {
                        while (refuelQueue.isEmpty()) {
                            lock.wait();
                        }

                        planeToRefuel = refuelQueue.poll();
                        System.out.println("⛽ Refueling: " + planeToRefuel.getName());

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                if (planeToRefuel != null) {
                    final Plane finalPlane = planeToRefuel;
                    try {
                        Thread.sleep(1000); // Refueling time
                        System.out.println("✅ Refueling Completed: " + finalPlane.getName());

                        // Use notifyAll instead of notify for more reliable thread wakeup
                        synchronized (finalPlane) {
                            finalPlane.notifyAll();

                            // Small delay to ensure notification is processed
                            Thread.sleep(100);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }
}