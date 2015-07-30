package com.github.stefanbirkner.jfortschritt;

/**
 * An additional part of a {@link ProgressLine} that is rendered after the
 * progress bar.
 *
 * @since 0.2.0
 */
public interface ProgressLinePart {

    /**
     * Called by the {@link ProgressLine} when it is started.
     *
     * @param numberOfSteps the total number of steps.
     * @since 0.2.0
     */
    void progressLineStarted(int numberOfSteps);

    /**
     * Returns the text that is appended to the progress line. The progress
     * line always calls {@link #progressLineStarted(int)} before calling this
     * method.
     *
     * @param step the number of the current step.
     * @return the text that is appended to the progress line.
     * @since 0.2.0
     */
    String getOutputForStep(int step);
}
