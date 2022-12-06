package br.ufsc.ine.ppgcc.population;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class WeightTest {

    @Test
    void constructor() {
        int[] input = {0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 1, 0, 1, 0, 1, 0, 1, 1, 1, 1, 1, 0, 0, 0};
        double expected = 404216;

        Weight actual = new Weight(input);

        assertEquals(expected, actual.getValue());
    }

}