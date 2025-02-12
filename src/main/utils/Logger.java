package utils;

public class Logger {
    public static synchronized void log(String message) {
        System.out.println(Thread.currentThread().getName() + ": " + message);
    }
}