package br.ufsc.ine.ppgcc.utils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;

public class ConverterUtil {

    public static int[] decimalToBinary(int number, boolean positive, int positionFloat)
    {
        String binaryString = Integer.toBinaryString(number);
        int[] binary = new int[36];

        int id = binary.length - 1;
        for (int i = binaryString.length() - 1; i >= 0; i--) {
            binary[id--] = Integer.parseInt(binaryString.charAt(i) + "");
        }

        binary[1] = positive ? 1 : 0;
        binary[0] = positionFloat;

        return binary;
    }
}
