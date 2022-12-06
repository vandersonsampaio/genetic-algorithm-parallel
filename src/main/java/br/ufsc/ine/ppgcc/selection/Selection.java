package br.ufsc.ine.ppgcc.selection;

import br.ufsc.ine.ppgcc.debug.Info;
import br.ufsc.ine.ppgcc.population.Individual;
import br.ufsc.ine.ppgcc.population.Parents;
import br.ufsc.ine.ppgcc.population.Population;
import br.ufsc.ine.ppgcc.reproduction.Reproduction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.stream.IntStream;

@Component
public class Selection {
    private static final Logger logger = LoggerFactory.getLogger(Selection.class);

    private final int numberPartitions;
    private final Population population;
    private final Reproduction reproduction;
    private final Random random;

    private final ExecutorService selectExecutor;

    public Selection(@Qualifier("number_partitions") int numberPartitions, Population population,
                     Reproduction reproduction, Random random,
                     @Qualifier("select_executors") ExecutorService selectExecutor) {
        this.numberPartitions = numberPartitions;
        this.population = population;
        this.reproduction = reproduction;
        this.random = random;
        this.selectExecutor = selectExecutor;
    }

    public void select() throws ExecutionException, InterruptedException {
        population.cleanSons();


        computeFitnessParallel();

        population.evolveGeneration();
    }

    private void computeFitnessParallel() throws InterruptedException, ExecutionException {
        List<Callable<String>> fitnessTasks = new ArrayList<>();
        IntStream.range(0, numberPartitions)
                .forEach(index -> fitnessTasks.add(() -> {
                    Info info = new Info();
                    for (int i = 0; i < population.getPopulationSize() / (2 * numberPartitions); i++) {
                        Parents parents = internalSelect(index);
                        population.addSon(reproduction.mutation(reproduction.crossover(parents)));
                        population.addSon(reproduction.mutation(reproduction.crossover(parents)));
                    }
                    info.finishCount();
                    logger.info("Partition: {} Time Select: {}", index, info.totalTime());
                    return "Success";
                }));

        List<Future<String>> futures = selectExecutor.invokeAll(fitnessTasks);

        for (Future<String> f : futures) {
            f.get();
        }
    }

    private Parents internalSelect(int partition) {
        double doubleRandom = random.nextDouble();
        Individual individualOne = population.findIndividualByProbabilityIsLessThan(partition, doubleRandom);

        Individual individualTwo = null;
        do {
            doubleRandom = random.nextDouble();
            individualTwo = population.findIndividualByProbabilityIsLessThan(partition, doubleRandom, individualOne);
        } while (individualOne == individualTwo);

        return new Parents(individualOne, individualTwo);
    }

}
