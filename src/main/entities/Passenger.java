package main.entities;

import java.util.Random;

public class Passenger extends Thread {
    private final int passengerId;
    private final String passengerName;
    private final boolean isArriving; // true = arriving passenger, false = departing passenger
    private final Plane plane;

    // Passenger types
    public static final int REGULAR = 0;
    public static final int PRIORITY = 1;
    public static final int SPECIAL_ASSISTANCE = 2;

    private final int passengerType;
    private static final Random random = new Random();

    // Passenger statistics (static to track across all passengers)
    private static int totalPassengersProcessed = 0;
    private static int totalBoardingTime = 0;
    private static int totalDeboardingTime = 0;

    public Passenger(int passengerId, Plane plane, boolean isArriving) {
        this.passengerId = passengerId;
        this.plane = plane;
        this.isArriving = isArriving;

        // Generate random passenger type (80% regular, 15% priority, 5% special assistance)
        int randomType = random.nextInt(100);
        if (randomType < 80) {
            this.passengerType = REGULAR;
            this.passengerName = "P" + passengerId;
        } else if (randomType < 95) {
            this.passengerType = PRIORITY;
            this.passengerName = "P" + passengerId + "-PRI";
        } else {
            this.passengerType = SPECIAL_ASSISTANCE;
            this.passengerName = "P" + passengerId + "-ASSIST";
        }
    }

    @Override
    public void run() {
        try {
            if (isArriving) {
                disembark();
            } else {
                board();
            }

            synchronized (Passenger.class) {
                totalPassengersProcessed++;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void board() throws InterruptedException {
        int boardingTime = calculateBoardingTime();

        // Simulate boarding process
        System.out.println("👤 " + getPassengerName() + " starting boarding on " + plane.getName());
        Thread.sleep(boardingTime);
        System.out.println("👤 " + getPassengerName() + " has boarded " + plane.getName());

        synchronized (Passenger.class) {
            totalBoardingTime += boardingTime;
        }
    }

    private void disembark() throws InterruptedException {
        int disembarkingTime = calculateDisembarkingTime();

        // Simulate disembarking process
        System.out.println("👤 " + getPassengerName() + " starting to disembark from " + plane.getName());
        Thread.sleep(disembarkingTime);
        System.out.println("👤 " + getPassengerName() + " has disembarked from " + plane.getName());

        synchronized (Passenger.class) {
            totalDeboardingTime += disembarkingTime;
        }
    }

    private int calculateBoardingTime() {
        // Base time for boarding
        int baseTime = 300;

        // Adjust by passenger type
        switch (passengerType) {
            case PRIORITY:
                return baseTime / 2; // Faster boarding for priority
            case SPECIAL_ASSISTANCE:
                return baseTime * 2; // Slower for special assistance
            default:
                return baseTime;
        }
    }

    private int calculateDisembarkingTime() {
        // Base time for disembarking
        int baseTime = 200;

        // Adjust by passenger type
        switch (passengerType) {
            case PRIORITY:
                return baseTime / 2; // Faster disembarking for priority
            case SPECIAL_ASSISTANCE:
                return baseTime * 2; // Slower for special assistance
            default:
                return baseTime;
        }
    }

    // Static methods to get passenger statistics
    public static synchronized int getTotalPassengersProcessed() {
        return totalPassengersProcessed;
    }

    public static synchronized double getAverageBoardingTime() {
        if (totalPassengersProcessed == 0) return 0;
        return (double) totalBoardingTime / totalPassengersProcessed;
    }

    public static synchronized double getAverageDisembarkingTime() {
        if (totalPassengersProcessed == 0) return 0;
        return (double) totalDeboardingTime / totalPassengersProcessed;
    }

    // Generate random passengers for a plane (between 1-50)
    public static Passenger[] generatePassengers(Plane plane, boolean isArriving, int count) {
        if (count <= 0 || count > 50) {
            count = random.nextInt(50) + 1; // Between 1-50 passengers
        }

        Passenger[] passengers = new Passenger[count];
        for (int i = 0; i < count; i++) {
            passengers[i] = new Passenger(
                    random.nextInt(10000), // Random passenger ID
                    plane,
                    isArriving
            );
        }

        return passengers;
    }

    // Reset statistics - useful for testing
    public static synchronized void resetStatistics() {
        totalPassengersProcessed = 0;
        totalBoardingTime = 0;
        totalDeboardingTime = 0;
    }

    public int getPassengerId() {
        return passengerId;
    }

    public String getPassengerName() {
        return passengerName;
    }

    public int getPassengerType() {
        return passengerType;
    }

    public boolean isArriving() {
        return isArriving;
    }
}