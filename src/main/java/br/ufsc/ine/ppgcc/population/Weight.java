package br.ufsc.ine.ppgcc.population;

import br.ufsc.ine.ppgcc.utils.ConverterUtil;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.stream.Collectors;

@Getter
public class Weight {

    private static final BigDecimal PRECISION = BigDecimal.valueOf(1_000_000);

    private BigDecimal value;
    private int[] chromosome;

    public Weight(BigDecimal value) {
        this.value = BigDecimal.valueOf(value.multiply(PRECISION).intValue() / PRECISION.doubleValue());
        setChromosome();
    }

    public Weight(int[] chromosome) {
        this.chromosome = chromosome;
        setValue();
    }

    private void setChromosome() {
        int positionFloat = String.valueOf(value).split("\\.")[1].length();
        boolean positive = value.doubleValue() >= 0;
        int number = Math.abs(value.multiply(PRECISION).intValue());

        chromosome = ConverterUtil.decimalToBinary(number, positive, positionFloat);
    }

    private void setValue() {
        int[] clone = chromosome.clone();
        clone[0] = clone[1] = 0;
        String number = Arrays.stream(clone).mapToObj(String::valueOf).collect(Collectors.joining(""));
        value = BigDecimal.valueOf(Integer.parseInt(number, 2));
        double multiply = chromosome[1] == 1 ? Math.pow(10, chromosome[0]) : Math.pow(10, chromosome[0]) * -1;
        value = value.divide(BigDecimal.valueOf(multiply));
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
