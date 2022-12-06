package br.ufsc.ine.ppgcc.population;

import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;

@Getter
public class Individual {

    private final Weight[] values;
    @Setter
    private double fitness;
    @Setter
    private double probability;

    public Individual(Weight[] values) {
        this.values = values;
    }

    @Override
    public String toString() {
        return "Individual=[" + Arrays.toString(values) + ", Fitness=" + fitness + "]";
    }
}
