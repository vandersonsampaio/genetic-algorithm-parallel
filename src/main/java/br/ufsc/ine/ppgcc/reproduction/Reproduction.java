package br.ufsc.ine.ppgcc.reproduction;

import br.ufsc.ine.ppgcc.population.Individual;
import br.ufsc.ine.ppgcc.population.Parents;
import br.ufsc.ine.ppgcc.population.Weight;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Random;
import java.util.stream.IntStream;

@Component
@RequiredArgsConstructor
public class Reproduction {

    private final Random random;

    public Individual crossover(Parents parents) {
        int zero = random.nextInt(2);
        Individual parentOne = parents.getParent(zero == 0);
        Individual parentTwo = parents.getParent(zero == 1);

        int lengthChromosome = parentOne.getValues()[0].getChromosome().length;
        int cutoff = lengthChromosome / 2;
        int[] chromosome = new int[lengthChromosome];
        Weight[] weightSon = new Weight[parentOne.getValues().length];

        for (int i = 0; i < parentOne.getValues().length; i++) {
            Weight valueOne = parentOne.getValues()[i];
            Weight valueTwo = parentTwo.getValues()[i];

            IntStream.range(0, cutoff).forEach(index -> chromosome[index] = valueOne.getChromosome()[index]);
            IntStream.range(cutoff, lengthChromosome).forEach(index -> chromosome[index] = valueTwo.getChromosome()[index]);

            weightSon[i] = new Weight(chromosome);
        }

        return new Individual(weightSon);
    }

    public Individual mutation(Individual individual) {
        //TO DO:
        return individual;
    }
}
