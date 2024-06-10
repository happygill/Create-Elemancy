package org.madscientists.createelemancy.foundation.util;

public class TickHelper {

    public static int secondsToTicks(double seconds) {
        return (int) (seconds * 20);
    }

    public static int minutesToTicks(double minutes) {
        return secondsToTicks(minutes * 60.0);
    }

}
