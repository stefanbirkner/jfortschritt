package com.github.stefanbirkner.jfortschritt;

/**
 * Displays the number of steps finished and the total number of steps.
 *
 * @since 0.2.0
 */
public class Counter implements ProgressLinePart {
    private int numberOfSteps;

    /**
     * Starts the counter by telling it the total number of steps.
     *
     * @param numberOfSteps the total number of steps.
     * @since 0.2.0
     */
    @Override
    public void progressLineStarted(int numberOfSteps) {
        this.numberOfSteps = numberOfSteps;
    }

    /**
     * Returns the counter output for the specific step. E.g.
     * <pre>(3/42)</pre>
     *
     * @param step the number of the current step.
     * @return the counter output for the specific step.
     * @since 0.2.0
     */
    @Override
    public String getOutputForStep(int step) {
        return "(" + step + "/" + numberOfSteps + ")";
    }
}
