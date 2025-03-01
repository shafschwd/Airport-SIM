package utils;

import java.util.ArrayList;
import java.util.List;

public class StatisticsCollector {
    private static final List<Long> waitingTimes = new ArrayList<>();

    public static synchronized void recordWaitingTime(long time) {
        waitingTimes.add(time);
    }

    public static void printStatistics() {
        if (waitingTimes.isEmpty()) return;

        long max = waitingTimes.stream().max(Long::compareTo).orElse(0L);
        long min = waitingTimes.stream().min(Long::compareTo).orElse(0L);
        double avg = waitingTimes.stream().mapToLong(Long::longValue).average().orElse(0);

        System.out.println("📊  Max Wait Time: " + max + " ms");
        System.out.println("📊  Min Wait Time: " + min + " ms");
        System.out.println("📊  Average Wait Time: " + avg + " ms");
        System.out.println("🚨 Emergency Landings Handled: " + waitingTimes.size());
    }
}
