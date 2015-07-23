package com.github.stefanbirkner.jfortschritt;

import static java.lang.Math.ceil;
import static java.lang.Math.max;
import static java.lang.System.out;

/**
 * A progress bar with a finite number of steps. The bar progresses by single
 * steps only. A {@code SingleStepProgressBar} is created by a
 * {@link SingleStepProgressBarBuilder}. (This is in order to avoid a multitude
 * of constructors when features get added.)
 * <h2>Usage</h2>
 * <p>Start the progress bar:
 * <pre>{@link #start() progressBar.start()};</pre>
 * <p>It immediately prints an empty progress bar:
 * <pre>[&gt;                                                              ] (0/42)</pre>
 * <p>The width of the line is always 72 chars. Now move the progress
 * bar forward.
 * <pre>{@link #moveForward() progressBar.moveForward()};</pre>
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
    private final int numberOfSteps;
    private boolean started = false;
    private int currentStep = 0;

    SingleStepProgressBar(int numberOfSteps) {
        this.numberOfSteps = numberOfSteps;
    }

    /**
     * Start the progress bar. This immediately prints an empty progress bar to
     * {@code System.in}. The progress bar can only be started once.
     *
     * @throws IllegalStateException if the progress bar has already
     *                               been started.
     * @since 0.1.0
     */
    public void start() throws IllegalStateException {
        assertNotAlreadyStarted();
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

    private void assertNotAlreadyComplete() throws IllegalStateException {
        if (isComplete())
            throw new IllegalStateException(
                "The progress cannot be moved forward because it is already complete.");
    }
}
