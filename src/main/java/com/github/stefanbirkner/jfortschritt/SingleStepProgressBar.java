package com.github.stefanbirkner.jfortschritt;

import static java.lang.Math.ceil;
import static java.lang.Math.max;
import static java.lang.System.out;

/**
 * A progress bar with a finite number of steps. The bar progresses by a single
 * step each time {@link #moveForward()} is called.
 * <h2>Usage</h2>
 * <p>Create a new progress bar and start it with the number of steps that are
 * needed for completion:
 * <pre>   SingleStepProgressbar progressBar = new {@code SingleStepProgressbar()};
 * progressBar.{@link #startWithNumberOfSteps(int) startWithNumberOfSteps(42)};</pre>
 * <p>It immediately prints an empty progress bar:
 * <pre>[&gt;                                                              ] (0/42)</pre>
 * <p>The width of the line is always 72 chars. Now move the progress
 * bar forward.
 * <pre>   progressBar.{@link #moveForward()};</pre>
 * <p>and see the result
 * <pre>[==&gt;                                                            ] (1/42)</pre>
 * <p>This is how a progress bar looks like at the end. (It renders a new line
 * character, too. Thus additional text starts at a new line.)
 * <pre>[=============================================================&gt;] (42/42)</pre>
 *
 * @since 0.1.0
 */
public class SingleStepProgressBar {
    private static final int WIDTH = 72;
    private int numberOfSteps;
    private boolean started = false;
    private int currentStep = 0;

    /**
     * Start the progress bar. This immediately prints an empty progress bar to
     * {@code System.in}. The progress bar can only be started once.
     *
     * @param numberOfSteps the number of steps that is needed for the progress
     *                      bar to be complete.
     * @throws IllegalArgumentException if {@code numberOfSteps} is negative.
     * @throws IllegalStateException if the progress bar has already
     *                               been started.
     * @since 0.2.0
     */
    public void startWithNumberOfSteps(int numberOfSteps) throws IllegalStateException {
        assertNonNegativeNumberOfSteps(numberOfSteps);
        assertNotAlreadyStarted();
        this.numberOfSteps = numberOfSteps;
        started = true;
        printProgressBar();
    }

    /**
     * Move the progress bar one step forward. This overrides the current
     * progress bar at {@code System.in} with the new one.
     *
     * @throws IllegalStateException if the progress bar is already complete.
     * @since 0.1.0
     */
    public void moveForward() throws IllegalStateException {
        assertNotAlreadyComplete();
        ++currentStep;
        deleteCurrentProgressBar();
        printProgressBar();
        printNewLineIfComplete();
    }

    private void deleteCurrentProgressBar() {
        out.print("\r");
    }

    private void printProgressBar() {
        String finalPart = "] (" + currentStep + "/" + numberOfSteps + ")";
        int numberOfEqualSigns = calculateNumberOfEqualSigns(finalPart);
        out.print("[");
        printCharNTimes('=', numberOfEqualSigns);
        out.print(">");
        printCharNTimes(' ', WIDTH - numberOfEqualSigns - 2 - finalPart.length());
        out.print(finalPart);
    }

    private int calculateNumberOfEqualSigns(String finalPart) {
        int maxNumberOfEqualSigns
            = WIDTH - finalPart.length() - 2 /* starting '[' and arrow '>' */;
        if (isComplete())
            return maxNumberOfEqualSigns;
        else
            return (int) ceil(
                currentStep * (maxNumberOfEqualSigns - 1)
                    / max(numberOfSteps - 1, 1d));
    }

    private void printCharNTimes(char c, int n) {
        for (int i = 0; i < n; ++i)
            out.print(c);
    }

    private void printNewLineIfComplete() {
        if (isComplete())
            out.println();
    }

    private boolean isComplete() {
        return currentStep == numberOfSteps;
    }

    private void assertNotAlreadyStarted() throws IllegalStateException {
        if (started)
            throw new IllegalStateException(
                "The progress bar has already been started and cannot be started twice.");
    }

    private void assertNonNegativeNumberOfSteps(int numberOfSteps) {
        if (numberOfSteps < 0)
            throw new IllegalArgumentException(
                "You cannot set number of steps to " + numberOfSteps
                    + " because it must be a non-negative number.");
    }

    private void assertNotAlreadyComplete() throws IllegalStateException {
        if (isComplete())
            throw new IllegalStateException(
                "The progress cannot be moved forward because it is already complete.");
    }
}
