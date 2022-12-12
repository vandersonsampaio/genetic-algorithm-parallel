package br.ufsc.ine.ppgcc.utils;

/**
 * Classe auxiliar responsável por mapear um valor decimal em um vetor cromossômico
 */
public class ConverterUtil {

    public static int[] decimalToBinary(int number, boolean positive, int positionFloat)
    {
        String binaryString = Integer.toBinaryString(number);
        int[] binary = new int[33];

        int id = binary.length - 1;
        for (int i = binaryString.length() - 1; i >= 0; i--) {
            binary[id--] = Integer.parseInt(binaryString.charAt(i) + "");
        }

        binary[1] = positive ? 1 : 0;
        binary[0] = positionFloat;

        return binary;
    }
}
