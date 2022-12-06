package br.ufsc.ine.ppgcc.selection;

import br.ufsc.ine.ppgcc.population.Individual;
import br.ufsc.ine.ppgcc.population.Parents;
import br.ufsc.ine.ppgcc.population.Population;
import br.ufsc.ine.ppgcc.reproduction.Reproduction;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Random;

@RequiredArgsConstructor
@Component
public class Selection implements Runnable {

    private final Population population;
    private final Reproduction reproduction;
    private final Random random;

    public void select() {
        population.cleanSons();

        for (int i = 0; i < population.getPopulationSize() / 2; i++) {
            Parents parents = internalSelect();
            population.addSon(reproduction.mutation(reproduction.crossover(parents)));
            population.addSon(reproduction.mutation(reproduction.crossover(parents)));
        }

        population.evolveGeneration();
    }

    private Parents internalSelect() {
        double doubleRandom = random.nextDouble();
        Individual individualOne = population.findIndividualByProbabilityIsLessThan(doubleRandom);

        Individual individualTwo = null;
        do {
            doubleRandom = random.nextDouble();
            individualTwo = population.findIndividualByProbabilityIsLessThan(doubleRandom, individualOne);
        } while (individualOne == individualTwo);

        return new Parents(individualOne, individualTwo);
    }

    @Override
    public void run() {
        select();
    }
}
