package controllers;

import utils.StatisticsCollector;

public class AirportManager {
    private int planesServed = 0;
    private int passengersBoarded = 0;

    public synchronized void recordPlaneService() {
        planesServed++;
    }

    public synchronized void recordPassengerBoarding(int count) {
        passengersBoarded += count;
    }

    public void printFinalStatistics() {
        System.out.println("--- Airport Final Statistics ---");
        System.out.println("Total Planes Served: " + planesServed);
        System.out.println("Total Passengers Boarded: " + passengersBoarded);
        StatisticsCollector.printStatistics();
    }
}