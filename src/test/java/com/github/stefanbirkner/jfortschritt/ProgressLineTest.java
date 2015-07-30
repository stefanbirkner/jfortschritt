package com.github.stefanbirkner.jfortschritt;

import static com.github.stefanbirkner.fishbowl.Fishbowl.exceptionThrownBy;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.github.stefanbirkner.fishbowl.Statement;
import de.bechte.junit.runners.context.HierarchicalContextRunner;
import org.assertj.core.api.AbstractCharSequenceAssert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.SystemOutRule;
import org.junit.runner.RunWith;

@RunWith(HierarchicalContextRunner.class)
public class ProgressLineTest {
    private static final int ARBITRARY_NUMBER_OF_STEPS = 42;
    private static final int NUMBER_OF_STEPS_WITH_MULTIPLE_STEPS_PER_CHARACTER = 1_000;

    private ProgressLine progressLine;

    @Rule
    public final SystemOutRule systemOutRule = new SystemOutRule().enableLog().mute();

    public class every_progress_line {
        @Before
        public void useProgressLineWithoutCounter() {
            progressLine = new ProgressLine();
        }

        @Test
        public void progress_line_cannot_be_started_with_negative_number_of_steps() {
            Throwable e = exceptionThrownBy(new Statement() {
                @Override
                public void evaluate() throws Throwable {
                    startProgressLineWithNumberOfSteps(-1);
                }
            });
            assertThat(e).isInstanceOf(IllegalArgumentException.class);
        }

        public class after_started {
            @Test
            public void progress_bar_is_empty() {
                startProgressLineWithArbitraryNumberOfSteps();
                assertTextVisibleAtSystemOut().startsWith("[> ").endsWith(" ]");
            }

            @Test
            public void progress_line_is_72_chars_long() {
                startProgressLineWithArbitraryNumberOfSteps();
                assertTextVisibleAtSystemOut().hasSize(72);
            }

            @Test
            public void progress_line_cannot_be_started_again() {
                startProgressLineWithArbitraryNumberOfSteps();
                Throwable e = exceptionThrownBy(new Statement() {
                    @Override
                    public void evaluate() throws Throwable {
                        startProgressLineWithArbitraryNumberOfSteps();
                    }
                });
                assertThat(e).isInstanceOf(IllegalStateException.class);
            }
        }

        public class after_first_step {
            @Test
            public void progress_bar_shows_at_least_one_char_progress() {
                startProgressLineWithArbitraryNumberOfSteps();
                moveProgressLineNSteps(1);
                assertTextVisibleAtSystemOut().startsWith("[=");
            }

            @Test
            public void progress_bar_shows_single_char_progress_for_very_small_proportion() {
                startProgressLineWithNumberOfSteps(
                    NUMBER_OF_STEPS_WITH_MULTIPLE_STEPS_PER_CHARACTER);
                moveProgressLineNSteps(1);
                assertTextVisibleAtSystemOut().startsWith("[=> ");
            }

            @Test
            public void progress_line_is_72_chars_long() {
                startProgressLineWithArbitraryNumberOfSteps();
                moveProgressLineNSteps(1);
                assertTextVisibleAtSystemOut().hasSize(72);
            }
        }

        public class after_second_to_last_step {
            @Test
            public void progress_bar_is_not_completely_filled() {
                //use a very large number of steps so that a single character is
                //more than a single step
                moveProgressLineToSecondToLastStep();
                assertTextVisibleAtSystemOut().endsWith("=> ]");
            }

            @Test
            public void progress_line_is_72_chars_long() {
                moveProgressLineToSecondToLastStep();
                assertTextVisibleAtSystemOut().hasSize(72);
            }

            private void moveProgressLineToSecondToLastStep() {
                startProgressLineWithNumberOfSteps(
                    NUMBER_OF_STEPS_WITH_MULTIPLE_STEPS_PER_CHARACTER);
                moveProgressLineNSteps(
                    NUMBER_OF_STEPS_WITH_MULTIPLE_STEPS_PER_CHARACTER - 1);
            }
        }

        public class after_last_step {
            @Test
            public void progress_bar_is_completely_filled() {
                //use a very large number of steps so that a single character is
                //more than a single step
                startProgressLineWithNumberOfSteps(1);
                moveProgressLineNSteps(1);
                assertTextVisibleAtSystemOut().contains("=>]");
            }

            @Test
            public void progress_line_is_terminated_with_new_line() {
                startProgressLineWithNumberOfSteps(42);
                moveProgressLineNSteps(42);
                assertTextVisibleAtSystemOut().endsWith("\n");
            }

            @Test
            public void progress_line_is_72_chars_long() {
                startProgressLineWithNumberOfSteps(ARBITRARY_NUMBER_OF_STEPS);
                moveProgressLineNSteps(ARBITRARY_NUMBER_OF_STEPS);
                assertTextVisibleAtSystemOut()
                    .hasSize(72 + 1 /* new line character */);
            }

            @Test
            public void progress_line_cannot_be_moved_forward() {
                startProgressLineWithNumberOfSteps(ARBITRARY_NUMBER_OF_STEPS);
                moveProgressLineNSteps(ARBITRARY_NUMBER_OF_STEPS);
                Throwable e = exceptionThrownBy(new Statement() {
                    @Override
                    public void evaluate() throws Throwable {
                        moveProgressLineNSteps(1);
                    }
                });
                assertThat(e).isInstanceOf(IllegalStateException.class);
            }
        }
    }

    public class progress_line_with_counter {
        private final Counter counter = mock(Counter.class);

        @Before
        public void useProgressLineWithCounter() {
            progressLine = new ProgressLine(counter);
        }

        @Test
        public void progress_line_separates_the_bar_and_the_counter_by_a_space() {
            useCounterWithOutpoutForStep("counter output", 0);
            startProgressLineWithArbitraryNumberOfSteps();
            assertTextVisibleAtSystemOut().contains("] counter");
        }

        @Test
        public void progress_line_renders_counter_at_the_end() {
            useCounterWithOutpoutForStep("counter output", 0);
            startProgressLineWithArbitraryNumberOfSteps();
            assertTextVisibleAtSystemOut().endsWith("counter output");
        }

        @Test
        public void progress_line_renders_counter_for_current_number_of_steps() {
            useCounterWithOutpoutForStep("counter output", 2);
            startProgressLineWithArbitraryNumberOfSteps();
            moveProgressLineNSteps(2);
            assertTextVisibleAtSystemOut().endsWith("counter output");
        }

        @Test
        public void progress_line_informs_counter_about_start() {
            startProgressLineWithNumberOfSteps(ARBITRARY_NUMBER_OF_STEPS);
            verify(counter).progressLineStarted(ARBITRARY_NUMBER_OF_STEPS);
        }

        @Test
        public void progress_line_is_72_chars_long() {
            useCounterWithOutpoutForStep("counter output", 0);
            startProgressLineWithArbitraryNumberOfSteps();
            assertTextVisibleAtSystemOut().hasSize(72);
        }

        private void useCounterWithOutpoutForStep(String output, int step) {
            when(counter.getOutputForStep(step)).thenReturn(output);
        }
    }

    private void startProgressLineWithNumberOfSteps(int numberOfSteps) {
        progressLine.startWithNumberOfSteps(numberOfSteps);
    }

    private void startProgressLineWithArbitraryNumberOfSteps() {
        startProgressLineWithNumberOfSteps(ARBITRARY_NUMBER_OF_STEPS);
    }

    private void moveProgressLineNSteps(int n) {
        for (int i = 0; i < n; ++i)
            progressLine.moveForward();
    }

    private AbstractCharSequenceAssert<?, String> assertTextVisibleAtSystemOut() {
        String log = systemOutRule.getLogWithNormalizedLineSeparator();
        int posOfLastCr = log.lastIndexOf("\r");
        String visibleText = log.substring(posOfLastCr + 1);
        return assertThat(visibleText);
    }
}
