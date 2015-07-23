package com.github.stefanbirkner.jfortschritt;

/**
 * Creates a new {@link SingleStepProgressBar}. Currently only the
 * number of steps can be chosen.
 *
 * <h2>Usage</h2>
 * <pre>SingleStepProgressBar progressBar = new SingleStepProgressBarBuilder()
 * .setNumberOfSteps(42)
 * .toProgressBar();
 * </pre>
 *
 * @since 0.1.0
 */
public class SingleStepProgressBarBuilder {
    private Integer numberOfSteps;

    /**
     * Set the number of steps that the progress bar needs to be completed.
     * @param numberOfSteps the number of steps.
     * @return the builder itself for method chaining.
     * @throws IllegalArgumentException if {@code numberOfSteps} is negative.
     * @since 0.1.0
     */
    public SingleStepProgressBarBuilder setNumberOfSteps(int numberOfSteps)
            throws IllegalArgumentException {
        if (numberOfSteps < 0)
            throw new IllegalArgumentException(
                "You cannot set number of steps to " + numberOfSteps
                + " because it must be a non-negative number.");
        this.numberOfSteps = numberOfSteps;
        return this;
    }

    /**
     * Create a {@code SingleStepProgressBar} with the specified number of
     * steps.
     * @return the new {@code SingleStepProgressBar}.
     * @throws IllegalStateException if {@link #setNumberOfSteps(int)} has not
     * been called before.
     * @since 0.1.0
     */
    public SingleStepProgressBar toProgressBar() {
        if (numberOfSteps == null)
            throw new IllegalStateException("The number of steps is not set.");
        return new SingleStepProgressBar(numberOfSteps);
    }
}
