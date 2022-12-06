package br.ufsc.ine.ppgcc.utils;

import br.ufsc.ine.ppgcc.debug.Info;
import br.ufsc.ine.ppgcc.population.Individual;
import br.ufsc.ine.ppgcc.population.Population;
import br.ufsc.ine.ppgcc.selection.Selection;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;

import java.util.stream.IntStream;

@RequiredArgsConstructor
@Service
public class GeneticAlgorithm {

    private static final Logger logger = LoggerFactory.getLogger(GeneticAlgorithm.class);
    private final Population population;
    private final Selection selection;
    private final TaskExecutor executor;

    public void compute(int maxGenerations) {
        Info info = new Info();

        population.startPopulation();
        info.resetStartTime();
        //executor.execute(population);
        population.computeIndividualMetrics();
        info.finishCount();
        logger.info("Initial Fitness: {}", info.totalTime());

        IntStream.range(0, maxGenerations).forEach( index -> {
            //executor.execute(selection);
            info.resetStartTime();
            selection.select();
            info.finishCount();
            logger.info(String.format("Gen %s Select: %d", index, info.totalTime()));
            //executor.execute(population);

            info.resetStartTime();
            population.computeIndividualMetrics();
            info.finishCount();
            logger.info(String.format("Gen %s Fitness: %d", index, info.totalTime()));
        });

        Individual better = population.mostPrepared();
        logger.info("Better Individual: {}", better);
        logger.info("Max Coefficient {}", better.getFitness());
    }
}
