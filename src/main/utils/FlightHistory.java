package utils;

import java.util.ArrayList;
import java.util.List;

public class FlightHistory {
    private static final List<String> flightLogs = new ArrayList<>();

    public static synchronized void logFlightEvent(String event) {
        flightLogs.add(event);
    }

    public static void printFlightTimeline() {
        System.out.println("\n=======================================");
        System.out.println("📅 FLIGHT TIMELINE");
        System.out.println("=======================================");
        for (String log : flightLogs) {
            System.out.println(log);
        }
        System.out.println("=======================================");
    }
}
