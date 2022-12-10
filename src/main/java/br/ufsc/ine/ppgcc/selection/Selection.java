package br.ufsc.ine.ppgcc.selection;

import br.ufsc.ine.ppgcc.debug.Info;
import br.ufsc.ine.ppgcc.population.Individual;
import br.ufsc.ine.ppgcc.population.Parents;
import br.ufsc.ine.ppgcc.population.Population;
import br.ufsc.ine.ppgcc.reproduction.Reproduction;
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

    private final int numberSelectPartitions;
    private final Population population;
    private final Reproduction reproduction;
    private final Random random;

    private final ExecutorService selectExecutor;

    public Selection(@Qualifier("number_select_partitions") int numberSelectPartitions,
                     Population population, Reproduction reproduction, Random random,
                     @Qualifier("select_executors") ExecutorService selectExecutor) {
        this.numberSelectPartitions = numberSelectPartitions;
        this.population = population;
        this.reproduction = reproduction;
        this.random = random;
        this.selectExecutor = selectExecutor;
    }

    public List<Long> select() throws ExecutionException, InterruptedException {
        population.cleanSons();

        List<Long> selectTime = computeSelectParallel();

        population.evolveGeneration();

        return selectTime;
    }

    private List<Long> computeSelectParallel() throws InterruptedException, ExecutionException {
        List<Long> selectTimes = new ArrayList<>();
        List<Callable<Long>> tasks = new ArrayList<>();

        IntStream.range(0, numberSelectPartitions)
                .forEach(index -> tasks.add(() -> {
                    Info info = new Info();
                    int length = population.getPopulationSize() / (2 * numberSelectPartitions);
                    population.probabilities(index);

                    for (int i = 0; i < length; i++) {
                        Parents parents = internalSelect(index);

                        population.addSon(reproduction.crossover(parents));
                    }
                    info.finishCount();
                    return info.totalTime();
                }));

        List<Future<Long>> futures = selectExecutor.invokeAll(tasks);

        for (Future<Long> f : futures) {
            selectTimes.add(f.get());
        }

        return selectTimes;
    }

    private Parents internalSelect(int partition) {
        double doubleRandom = random.nextDouble();
        Individual individualOne = population.findIndividualByProbabilityIsLessThan(partition, doubleRandom);

        Individual individualTwo;
        do {
            doubleRandom = random.nextDouble();
            individualTwo = population.findIndividualByProbabilityIsLessThan(partition, doubleRandom);
        } while (individualOne == individualTwo);

        return new Parents(individualOne, individualTwo);
    }

}
