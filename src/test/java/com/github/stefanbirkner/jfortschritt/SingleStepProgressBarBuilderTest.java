package com.github.stefanbirkner.jfortschritt;

import com.github.stefanbirkner.fishbowl.Statement;
import org.junit.Test;

import static com.github.stefanbirkner.fishbowl.Fishbowl.exceptionThrownBy;
import static org.assertj.core.api.Assertions.assertThat;

public class SingleStepProgressBarBuilderTest {
    @Test
    public void a_progress_bar_cannot_be_created_with_negative_number_of_steps() {
        final SingleStepProgressBarBuilder builder = new SingleStepProgressBarBuilder();
        Throwable e = exceptionThrownBy(new Statement() {
            @Override
            public void evaluate() throws Throwable {
                builder.setNumberOfSteps(-1);
            }
        });
        assertThat(e).isInstanceOf(IllegalArgumentException.class);
    }


    @Test
    public void a_progress_bar_cannot_be_created_without_specifying_the_number_of_steps() {
        final SingleStepProgressBarBuilder builder = new SingleStepProgressBarBuilder();
        Throwable e = exceptionThrownBy(new Statement() {
            @Override
            public void evaluate() throws Throwable {
                builder.toProgressBar();
            }
        });
        assertThat(e).isInstanceOf(IllegalStateException.class);
    }
}
