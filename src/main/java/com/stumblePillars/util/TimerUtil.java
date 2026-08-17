package com.stumblePillars.util;

import java.util.concurrent.TimeUnit;

public class TimerUtil {

    public static String refactor(int countdownInSeconds){
        final long minutes = TimeUnit.SECONDS.toMinutes(countdownInSeconds);
        final long seconds = TimeUnit.SECONDS.toSeconds(countdownInSeconds - TimeUnit.MINUTES.toSeconds(minutes));
        return String.format("%02dmin:%02dseg",minutes,seconds);
    }

}
