package br.ufsc.ine.ppgcc.utils;

import br.ufsc.ine.ppgcc.debug.Info;
import br.ufsc.ine.ppgcc.population.Individual;
import br.ufsc.ine.ppgcc.population.Population;
import br.ufsc.ine.ppgcc.selection.Selection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.stream.IntStream;

@Service
public class GeneticAlgorithm {

    private static final Logger logger = LoggerFactory.getLogger(GeneticAlgorithm.class);
    private final Population population;
    private final Selection selection;
    private final ExecutorService fitnessExecutor;

    private final Integer fitnessPartitions;

    private final List<Long> fitnessTime = new ArrayList<>();
    private final List<Long> selectTime = new ArrayList<>();

    @Value("${ga-parallel.population.generation}")
    private Integer generations;

    public GeneticAlgorithm(Population population, Selection selection,
                            @Qualifier("fitness_executors") ExecutorService fitnessExecutor,
                            @Qualifier("number_fitness_partitions") Integer fitnessPartitions) {
        this.population = population;
        this.selection = selection;
        this.fitnessExecutor = fitnessExecutor;
        this.fitnessPartitions = fitnessPartitions;
    }

    public void compute() throws ExecutionException, InterruptedException {
        Info infoProcess = new Info();

        population.startPopulation();
        computeFitnessParallel();

        for (int index = 0; index < generations; index++) {
            selectTime.addAll(selection.select());
            computeFitnessParallel();
        }

        infoProcess.finishCount();
        Individual better = population.mostPrepared();
        logger.info("Better Individual: {}", better);
        logger.info("Max Coefficient {}", better.getFitness());
        logger.info("Total Time: {}", infoProcess.totalTime());
        logger.info("Total Fitness Time: {}", fitnessTime.stream().mapToLong(i -> i).average().getAsDouble());
        logger.info("Total Select Time: {}", selectTime.stream().mapToLong(i -> i).average().getAsDouble());
        logger.info("List Fitness Time: {}", Arrays.toString(fitnessTime.toArray()));
        logger.info("List Select Time: {}", Arrays.toString(selectTime.toArray()));

    }

    private void computeFitnessParallel() throws InterruptedException, ExecutionException {
        List<Callable<String>> fitnessTasks = new ArrayList<>();
        IntStream.range(0, fitnessPartitions)
                .forEach(index -> fitnessTasks.add(() -> {
                    fitnessTime.add(population.computeFitness(index));
                    return "Success";
                }));

        List<Future<String>> futures = fitnessExecutor.invokeAll(fitnessTasks);

        for (Future<String> f : futures) {
            f.get();
        }
    }
}
