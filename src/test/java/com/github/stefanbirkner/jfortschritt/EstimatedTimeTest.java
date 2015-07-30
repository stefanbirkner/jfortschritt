package com.github.stefanbirkner.jfortschritt;

import com.squareup.burst.BurstJUnit4;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(BurstJUnit4.class)
public class EstimatedTimeTest {
    private static final int ARBITRARY_NUMBER_OF_STEPS = 54687;
    private static final int ARBITRARY_ELAPSED_TIME = 496;

    private enum TestCase {
        nothing_is_shown_before_first_step(
            0, ARBITRARY_NUMBER_OF_STEPS, ARBITRARY_ELAPSED_TIME, ""),
        estimated_time_is_at_least_one_second(
            9, 10, 1, "eta 1s"),
        estimated_time_is_without_minutes_if_it_is_less_than_a_minute(
            9, 10, minutes(9) - seconds(10), "eta 59s"),
        estimated_time_is_without_hours_if_it_is_less_than_an_hour(
            9, 10, hours(9) - seconds(10), "eta 59m 59s"),
        estimated_time_is_without_seconds_if_it_is_at_least_one_hour(
            9, 10, hours(9), "eta 1h 0m"),
        elapsed_time_is_shown_if_finished(
            ARBITRARY_NUMBER_OF_STEPS, ARBITRARY_NUMBER_OF_STEPS, seconds(59), "in 59s");

        final int currentStep;
        final int totalNumberOfSteps;
        final long elapsedTime;
        final String expectedOutput;

        TestCase(int currentStep, int totalNumberOfSteps, long elapsedTime, String expectedOutput) {
            this.currentStep = currentStep;
            this.totalNumberOfSteps = totalNumberOfSteps;
            this.elapsedTime = elapsedTime;
            this.expectedOutput = expectedOutput;
        }

        static long seconds(int n) {
            return n * 1_000;
        }

        static long minutes(int n) {
            return n * seconds(60);
        }

        static long hours(int n) {
            return n * minutes(60);
        }
    }

    @Test
    public void verifyTestCase(TestCase testCase) {
        EstimatedTimeWithManagedSystemTime estimatedTime = new EstimatedTimeWithManagedSystemTime();
        estimatedTime.progressLineStarted(testCase.totalNumberOfSteps);
        estimatedTime.currentTime += testCase.elapsedTime;
        String output = estimatedTime.getOutputForStep(testCase.currentStep);
        assertThat(output).isEqualTo(testCase.expectedOutput);
    }

    private static class EstimatedTimeWithManagedSystemTime extends EstimatedTime {
        long currentTime = 54984658; //arbitrary time stamp

        @Override
        long getTime() {
            return currentTime;
        }
    }
}
