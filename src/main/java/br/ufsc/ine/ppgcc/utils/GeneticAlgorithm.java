package br.ufsc.ine.ppgcc.utils;

import br.ufsc.ine.ppgcc.debug.Info;
import br.ufsc.ine.ppgcc.population.Individual;
import br.ufsc.ine.ppgcc.population.Population;
import br.ufsc.ine.ppgcc.selection.Selection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.stream.IntStream;

@Service
public class GeneticAlgorithm {

    private static final int NUMBER_EXECUTORS = 2;
    private static final Logger logger = LoggerFactory.getLogger(GeneticAlgorithm.class);
    private final Population population;
    private final Selection selection;
    private final ExecutorService fitnessExecutor;

    public GeneticAlgorithm(Population population, Selection selection,
                            @Qualifier("fitness_executors") ExecutorService fitnessExecutor) {
        this.population = population;
        this.selection = selection;
        this.fitnessExecutor = fitnessExecutor;
    }

    public void compute(int maxGenerations) throws ExecutionException, InterruptedException {
        Info info = new Info();

        population.startPopulation();

        info.resetStartTime();
        computeFitnessParallel();
        info.finishCount();

        logger.info("Initial Fitness: {}", info.totalTime());

        for (int index = 0; index < maxGenerations; index++) {
            info.resetStartTime();
            selection.select();
            info.finishCount();
            logger.info(String.format("Gen %s Select: %d", index, info.totalTime()));

            info.resetStartTime();
            computeFitnessParallel();
            info.finishCount();
            logger.info(String.format("Gen %s Fitness: %d", index, info.totalTime()));
        }

        Individual better = population.mostPrepared();
        logger.info("Better Individual: {}", better);
        logger.info("Max Coefficient {}", better.getFitness());
    }

    private void computeFitnessParallel() throws InterruptedException, ExecutionException {
        List<Callable<String>> fitnessTasks = new ArrayList<>();
        IntStream.range(0, NUMBER_EXECUTORS)
                .forEach(index -> fitnessTasks.add(() -> {
                    population.computeIndividualMetrics(index);
                    return "Success";
                }));

        List<Future<String>> futures = fitnessExecutor.invokeAll(fitnessTasks);

        for (Future<String> f : futures) {
            f.get();
        }
    }
}
