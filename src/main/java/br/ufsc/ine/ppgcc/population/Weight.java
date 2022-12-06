package br.ufsc.ine.ppgcc.population;

import br.ufsc.ine.ppgcc.utils.ConverterUtil;
import lombok.Getter;

import java.util.Arrays;
import java.util.stream.Collectors;

@Getter
public class Weight {

    private static final int PRECISION = 1_000_000;
    private double value;
    private int[] chromosome;

    public Weight(double value) {
        this.value =  ((int) (value * PRECISION)) / (double) PRECISION;
        setChromosome();
    }

    public Weight(int[] chromosome) {
        this.chromosome = chromosome;
        setValue();
    }

    private void setChromosome() {
        int positionFloat = String.valueOf(value).split("\\.")[1].length();
        boolean positive = value >= 0;
        int number = Math.abs((int) (value * PRECISION));

        chromosome = ConverterUtil.decimalToBinary(number, positive, positionFloat);
    }

    private void setValue() {
        int[] clone = chromosome.clone();
        clone[0] = clone[1] = 0;
        String number = Arrays.stream(clone).mapToObj(String::valueOf).collect(Collectors.joining(""));
        value = Integer.parseInt(number, 2);
        value *= chromosome[1] == 1 ? Math.pow(10, chromosome[0]) : Math.pow(10, chromosome[0]) * -1;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
