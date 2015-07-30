package com.github.stefanbirkner.jfortschritt;

import static java.lang.Math.ceil;
import static java.lang.Math.max;
import static java.lang.System.out;

/**
 * A progress line shows the progress of a task using a single line on the
 * console. It's made up of a progress bar and a counter.
 * <h2>Usage</h2>
 * <p>Create a new progress line and start it with the number of steps that are
 * needed for completion:
 * <pre>   ProgressLine progressLine = new {@code ProgressLine()};
 * progressLine.{@link #startWithNumberOfSteps(int) startWithNumberOfSteps(42)};</pre>
 * <p>It immediately prints an empty progress bar and the counter:
 * <pre>[&gt;                                                                     ]</pre>
 * <p>The width of the line is always 72 chars. Now move the progress line
 * forward.
 * <pre>   progressLine.{@link #moveForward()};</pre>
 * <p>and see the result
 * <pre>[==&gt;                                                                   ]</pre>
 * <p>This is how a progress line looks like at the end. (It renders a new line
 * character, too. Thus additional text starts at a new line.)
 * <pre>[==================================================================== &gt;]</pre>
 * <p>You may add additional parts to the progress line. E.g. JFortschritt
 * provides a {@link Counter}, but you can build your own parts by implementing
 * the interface {@link ProgressLinePart}.
 * <pre>ProgressLine progressLine = new ProgressLine(new Counter());</pre>
 * <p>This is how a progress line looks with the additional parts.
 * <pre>[==&gt;                                                            ] (1/42)</pre>
 *
 * @since 0.1.0
 */
public class ProgressLine {
    private static final int WIDTH = 72;
    private final ProgressLinePart[] additionalParts;
    private int numberOfSteps;
    private boolean started = false;
    private int currentStep = 0;

    /**
     * Create a progress line with a progress bar and an additional
     * {@link ProgressLinePart}s. These parts are rendered after the the
     * progress bar and are separated by a whitespace.
     * @param additionalParts additional parts that are rendered after
     *                        the progress bar.
     */
    public ProgressLine(ProgressLinePart... additionalParts) {
        this.additionalParts = additionalParts;
    }

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
        informCounterAboutStart(numberOfSteps);
        printProgressBar();
    }

    private void informCounterAboutStart(int numberOfSteps) {
        for (ProgressLinePart part : additionalParts)
            part.progressLineStarted(numberOfSteps);
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
        String finalPart = "]" + getOutputForParts();
        int numberOfEqualSigns = calculateNumberOfEqualSigns(finalPart);
        out.print("[");
        printCharNTimes('=', numberOfEqualSigns);
        out.print(">");
        printCharNTimes(' ', WIDTH - numberOfEqualSigns - 2 - finalPart.length());
        out.print(finalPart);
    }

    public String getOutputForParts() {
        StringBuilder output = new StringBuilder();
        for (ProgressLinePart part : additionalParts)
            output.append(" ").append(part.getOutputForStep(currentStep));
        return output.toString();
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
