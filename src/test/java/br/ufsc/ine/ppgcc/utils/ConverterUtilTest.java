package br.ufsc.ine.ppgcc.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ConverterUtilTest {

    @Test
    void decimalToBinary() {
        int[] input = {0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 1, 0, 1, 0, 1, 0, 1, 1, 1, 1, 1, 0, 0, 0};
        int number = 404216;

        int[] actual = ConverterUtil.decimalToBinary(number, true, 0);

        assertArrayEquals(actual, input);
    }
}