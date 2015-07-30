package com.github.stefanbirkner.jfortschritt;

import static java.lang.Math.max;

/**
 * Displays the estimated time until the progress line is complete. After
 * completion it displays the time elapsed since start.
 *
 * @since 0.2.0
 */
public class EstimatedTime implements ProgressLinePart {
    private static final int ONE_SECOND = 1_000;
    private int numberOfSteps;
    private long start;

    /**
     * Starts the component by telling it the total number of steps.
     *
     * @param numberOfSteps the total number of steps.
     * @since 0.2.0
     */
    @Override
    public void progressLineStarted(int numberOfSteps) {
        this.numberOfSteps = numberOfSteps;
        this.start = getTime();
    }

    /**
     * Returns the estimated time that is needed for completion. E.g.
     * <pre>eta 10s</pre>
     * After completion it displays the time elapsed since start. E.g.
     * <pre>in 2m 10s</pre>
     *
     * @param step the number of the current step.
     * @return the estimated time or the time elapsed since start.
     * @since 0.2.0
     */
    @Override
    public String getOutputForStep(int step) {
        if (step == 0)
            return "";
        else if (step < numberOfSteps)
            return getEstimatedTime(step);
        else
            return getTimeWhenComplete();
    }

    private String getEstimatedTime(int step) {
        int remainingSteps = numberOfSteps - step;
        long remainingTime = remainingSteps * getElapsedTime() / step;
        return "eta " + format(remainingTime);
    }

    private String getTimeWhenComplete() {
        return "in " + format(getElapsedTime());
    }

    private long getElapsedTime() {
        long elapsedTime = getTime() - start;
        return max(ONE_SECOND, elapsedTime);
    }

    private String format(long time) {
        long seconds = (time + ONE_SECOND - 1) / ONE_SECOND;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        if (minutes == 0)
            return secondsWithoutMinutes(seconds);
        else if (hours == 0)
            return minutesWithoutHours(minutes) + " " + secondsWithoutMinutes(seconds);
        else
            return hours(hours) + " " + minutesWithoutHours(minutes);
    }

    private String secondsWithoutMinutes(long seconds) {
        return seconds % 60 + "s";
    }

    private String minutesWithoutHours(long minutes) {
        return minutes % 60 + "m";
    }

    private String hours(long hours) {
        return hours + "h";
    }

    //is used to override time in tests.
    long getTime() {
        return System.currentTimeMillis();
    }
}
