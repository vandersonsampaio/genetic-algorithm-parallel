package br.ufsc.ine.ppgcc.population;

import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;

/**
 * Entidade que representa um indivíduo na população do AG.
 * Cada indivíduo é composto por um conjunto de pesos (values do tipo Weight), um valor de aptidão (Fitness)
 * e probabilidade de seleção (Probability)
 */
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
