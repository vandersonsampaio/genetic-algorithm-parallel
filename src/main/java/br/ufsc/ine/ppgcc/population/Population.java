package br.ufsc.ine.ppgcc.population;

import br.ufsc.ine.ppgcc.debug.Info;
import br.ufsc.ine.ppgcc.utils.EngineUtil;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

@Component
public class Population {

    private static final Logger logger = LoggerFactory.getLogger(Population.class);
    private static final Integer NUMBER_WEIGHTS = 10;

    private final int numberPartitions;
    private final EngineUtil engineUtil;
    @Getter
    private final Integer populationSize;
    @Getter
    private List<Individual> individuals;

    private final List<List<Individual>> individualsSplited;

    private List<Individual> sons;

    public Population(EngineUtil engineUtil, @Qualifier("population_size") Integer populationSize,
                      @Qualifier("number_partitions") int numberPartitions) {
        this.engineUtil = engineUtil;
        this.populationSize = populationSize;
        this.numberPartitions = numberPartitions;
        individuals = new ArrayList<>();
        sons = new ArrayList<>();
        individualsSplited = new ArrayList<>();
    }

    public void startPopulation() {
        IntStream.range(0, populationSize)
                .forEach(index -> individuals.add(engineUtil.generateIndividual(NUMBER_WEIGHTS)));

        splitIndividuals();
    }

    private void splitIndividuals() {
        individualsSplited.clear();
        int divisor = populationSize / numberPartitions;

        IntStream.range(0, numberPartitions)
                .forEach(i ->
                        individualsSplited.add(individuals.subList(i * divisor, i * divisor + divisor)));
    }

    public void computeIndividualMetrics(int partition) {
        Info info = new Info();
        computeFitness(partition);
        probabilities(partition);

        info.finishCount();
        logger.info("Partition: {} - Time Fitness: {}", partition, info.totalTime());
    }

    private void computeFitness(int position) {
        List<Individual> individualPart = individualsSplited.get(position);
        IntStream.range(0, individualPart.size()).forEach(index -> {
            double[] computeData = engineUtil.computeMetric(individualPart.get(index));
            individualPart.get(index).setFitness(engineUtil.computeCoefficient(computeData));
        });
    }

    private void probabilities(int partition) {
        double criteriaTotal = individualsSplited.get(partition)
                .stream().mapToDouble(Individual::getFitness).sum();

        double range = 0D;
        for (Individual individual : individualsSplited.get(partition)) {
            double aux = individual.getFitness() / criteriaTotal;
            range += aux;
            individual.setProbability(range);
        }
    }

    public void evolveGeneration() {
        individuals = sons;
        splitIndividuals();
    }

    public void cleanSons() {
        sons = new ArrayList<>();
    }

    public synchronized void addSon(Individual individual) {
        sons.add(individual);
    }

    public Individual findIndividualByProbabilityIsLessThan(int partition, double probability) {
        return individualsSplited.get(partition).stream()
                .filter(individual -> individual.getProbability() >= probability)
                .findAny().orElseThrow();
    }

    public Individual findIndividualByProbabilityIsLessThan(int partition, double probability, Individual ind) {
        return individualsSplited.get(partition).stream()
                .filter(individual -> individual.getProbability() >= probability && individual != ind)
                .findAny().orElseThrow();
    }

    public Individual mostPrepared() {
        double maxFitness = individuals.stream().mapToDouble(Individual::getFitness).max().orElseThrow();

        return individuals.stream().filter(individual -> individual.getFitness() == maxFitness).findAny().orElseThrow();
    }
}
