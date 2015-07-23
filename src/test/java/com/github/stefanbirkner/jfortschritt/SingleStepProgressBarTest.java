package com.github.stefanbirkner.jfortschritt;

import static com.github.stefanbirkner.fishbowl.Fishbowl.exceptionThrownBy;
import static org.assertj.core.api.Assertions.*;

import com.github.stefanbirkner.fishbowl.Statement;
import de.bechte.junit.runners.context.HierarchicalContextRunner;
import org.assertj.core.api.AbstractCharSequenceAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.SystemOutRule;
import org.junit.runner.RunWith;

@RunWith(HierarchicalContextRunner.class)
public class SingleStepProgressBarTest {
    private static final int ARBITRARY_NUMBER_OF_STEPS = 42;
    private static final int NUMBER_OF_STEPS_WITH_MULTIPLE_STEPS_PER_CHARACTER = 1_000;

    private SingleStepProgressBar progressBar;

    @Rule
    public final SystemOutRule systemOutRule = new SystemOutRule().enableLog();

    public class AfterStarted {
        @Test
        public void progress_bar_is_empty() {
            createProgressBarWithArbitraryNumberOfSteps();
            startProgressBar();
            assertTextVisibleAtSystemOut().startsWith("[> ").contains(" ]");
        }

        @Test
        public void progress_bar_shows_zero_out_of_n_steps_finished() {
            createProgressBarWithNumberOfSteps(1);
            startProgressBar();
            assertTextVisibleAtSystemOut().endsWith("(0/1)");
        }

        @Test
        public void progress_bar_separates_the_bar_itself_and_the_count_by_a_space() {
            createProgressBarWithArbitraryNumberOfSteps();
            startProgressBar();
            assertBarAndCountAreSeparatedByASpace();
        }

        @Test
        public void progress_bar_is_72_chars_long() {
            createProgressBarWithArbitraryNumberOfSteps();
            startProgressBar();
            assertTextVisibleAtSystemOut().hasSize(72);
        }

        @Test
        public void progress_bar_cannot_be_started_again() {
            createProgressBarWithArbitraryNumberOfSteps();
            startProgressBar();
            Throwable e = exceptionThrownBy(new Statement() {
                @Override
                public void evaluate() throws Throwable {
                    startProgressBar();
                }
            });
            assertThat(e).isInstanceOf(IllegalStateException.class);
        }
    }

    public class AfterFirstStep {
        @Test
        public void progress_bar_shows_at_least_one_char_progress() {
            createProgressBarWithArbitraryNumberOfSteps();
            moveProgressBarNSteps(1);
            assertTextVisibleAtSystemOut().startsWith("[=");
        }

        @Test
        public void progress_bar_shows_single_char_progress_for_very_small_proportion() {
            createProgressBarWithNumberOfSteps(
                NUMBER_OF_STEPS_WITH_MULTIPLE_STEPS_PER_CHARACTER);
            moveProgressBarNSteps(1);
            assertTextVisibleAtSystemOut().startsWith("[=> ");
        }

        @Test
        public void progress_bar_shows_one_out_of_n_steps_finished() {
            createProgressBarWithNumberOfSteps(2);
            moveProgressBarNSteps(1);
            assertTextVisibleAtSystemOut().endsWith("(1/2)");
        }

        @Test
        public void progress_bar_separates_the_bar_itself_and_the_count_by_a_space() {
            createProgressBarWithArbitraryNumberOfSteps();
            moveProgressBarNSteps(1);
            assertBarAndCountAreSeparatedByASpace();
        }

        @Test
        public void progress_bar_is_72_chars_long() {
            createProgressBarWithArbitraryNumberOfSteps();
            moveProgressBarNSteps(1);
            assertTextVisibleAtSystemOut().hasSize(72);
        }
    }

    public class AfterSecondToLastStep {
        @Test
        public void progress_bar_is_not_completely_filled() {
            //use a very large number of steps so that a single character is
            //more than a single step
            createProgressBar();
            moveProgressBarToSecondToLastStep();
            assertTextVisibleAtSystemOut().contains("=> ]");
        }

        @Test
        public void progress_bar_shows_n_minus_1_out_of_n_steps_finished() {
            createProgressBar();
            moveProgressBarToSecondToLastStep();
            assertTextVisibleAtSystemOut().endsWith("(999/1000)");
        }

        @Test
        public void progress_bar_separates_the_bar_itself_and_the_count_by_a_space() {
            createProgressBar();
            moveProgressBarToSecondToLastStep();
            assertBarAndCountAreSeparatedByASpace();
        }

        @Test
        public void progress_bar_is_72_chars_long() {
            createProgressBar();
            moveProgressBarToSecondToLastStep();
            assertTextVisibleAtSystemOut().hasSize(72);
        }

        private void createProgressBar() {
            createProgressBarWithNumberOfSteps(
                NUMBER_OF_STEPS_WITH_MULTIPLE_STEPS_PER_CHARACTER);
        }

        private void moveProgressBarToSecondToLastStep() {
            moveProgressBarNSteps(
                NUMBER_OF_STEPS_WITH_MULTIPLE_STEPS_PER_CHARACTER - 1);
        }
    }

    public class AfterLastStep {
        @Test
        public void progress_bar_is_completely_filled() {
            //use a very large number of steps so that a single character is
            //more than a single step
            createProgressBarWithNumberOfSteps(1);
            moveProgressBarNSteps(1);
            assertTextVisibleAtSystemOut().contains("=>]");
        }

        @Test
        public void progress_bar_shows_n_out_of_n_steps_finished() {
            createProgressBarWithNumberOfSteps(42);
            moveProgressBarNSteps(42);
            assertTextVisibleAtSystemOut().contains("(42/42)");
        }

        @Test
        public void progress_bar_is_terminated_with_new_line() {
            createProgressBarWithNumberOfSteps(42);
            moveProgressBarNSteps(42);
            assertTextVisibleAtSystemOut().endsWith("\n");
        }

        @Test
        public void progress_bar_separates_the_bar_itself_and_the_count_by_a_space() {
            createProgressBarWithNumberOfSteps(ARBITRARY_NUMBER_OF_STEPS);
            moveProgressBarNSteps(ARBITRARY_NUMBER_OF_STEPS);
            assertBarAndCountAreSeparatedByASpace();
        }

        @Test
        public void progress_bar_is_72_chars_long() {
            createProgressBarWithNumberOfSteps(ARBITRARY_NUMBER_OF_STEPS);
            moveProgressBarNSteps(ARBITRARY_NUMBER_OF_STEPS);
            assertTextVisibleAtSystemOut()
                .hasSize(72 + 1 /* new line character */);
        }

        @Test
        public void progress_bar_cannot_be_moved_forward() {
            createProgressBarWithNumberOfSteps(ARBITRARY_NUMBER_OF_STEPS);
            moveProgressBarNSteps(ARBITRARY_NUMBER_OF_STEPS);
            Throwable e = exceptionThrownBy(new Statement() {
                @Override
                public void evaluate() throws Throwable {
                    moveProgressBarNSteps(1);
                }
            });
            assertThat(e).isInstanceOf(IllegalStateException.class);
        }
    }

    private void createProgressBarWithNumberOfSteps(int numberOfSteps) {
        progressBar = new SingleStepProgressBarBuilder()
            .setNumberOfSteps(numberOfSteps)
            .toProgressBar();
    }

    private void createProgressBarWithArbitraryNumberOfSteps() {
        createProgressBarWithNumberOfSteps(ARBITRARY_NUMBER_OF_STEPS);
    }

    private void startProgressBar() {
        progressBar.start();
    }

    private void moveProgressBarNSteps(int n) {
        for (int i = 0; i < n; ++i)
            progressBar.moveForward();
    }

    private AbstractCharSequenceAssert<?, String> assertTextVisibleAtSystemOut() {
        String log = systemOutRule.getLogWithNormalizedLineSeparator();
        int posOfLastCr = log.lastIndexOf("\r");
        String visibleText = log.substring(posOfLastCr + 1);
        return assertThat(visibleText);
    }

    private void assertBarAndCountAreSeparatedByASpace() {
        assertTextVisibleAtSystemOut().contains("] (");
    }
}
