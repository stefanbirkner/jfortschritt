package com.github.stefanbirkner.jfortschritt;

import static org.assertj.core.api.Assertions.*;

import org.junit.Test;


public class CounterTest {
    private final Counter counter = new Counter();

    @Test
    public void counter_shows_x_out_of_n_steps_put_in_parentheses() {
        counter.progressLineStarted(42);
        String output = counter.getOutputForStep(5);
        assertThat(output).isEqualTo("(5/42)");
    }
}
