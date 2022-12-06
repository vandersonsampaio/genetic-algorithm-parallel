package br.ufsc.ine.ppgcc.population;

import br.ufsc.ine.ppgcc.utils.EngineUtil;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

@Component
public class Population implements Runnable {

    private static final Integer NUMBER_WEIGHTS = 10;
    private final EngineUtil engineUtil;
    @Getter
    private final Integer populationSize;
    @Getter
    private List<Individual> individuals;
    private List<Individual> sons;

    public Population(EngineUtil engineUtil, Integer populationSize) {
        this.engineUtil = engineUtil;
        this.populationSize = populationSize;
        individuals = new ArrayList<>();
        sons = new ArrayList<>();
    }

    public void startPopulation() {
        IntStream.range(0, populationSize)
                .forEach(index -> individuals.add(engineUtil.generateIndividual(NUMBER_WEIGHTS)));
    }

    public void computeIndividualMetrics() {
        computeFitness();
        probabilities();
    }

    private void computeFitness() {
        IntStream.range(0, populationSize).forEach(index -> {
            double[] computeData = engineUtil.computeMetric(individuals.get(index));
            individuals.get(index).setFitness(engineUtil.computeCoefficient(computeData));
        });
    }

    private void probabilities() {
        double criteriaTotal = individuals.stream().mapToDouble(Individual::getFitness).sum();

        double range = 0D;
        for (int i = 0; i < populationSize; i++) {
            double aux = individuals.get(i).getFitness() / criteriaTotal;
            range += aux;
            individuals.get(i).setProbability(range);
        }
    }

    public void evolveGeneration() {
        individuals = sons;
    }

    public void cleanSons() {
        sons = new ArrayList<>();
    }

    public void addSon(Individual individual) {
        sons.add(individual);
    }

    public Individual findIndividualByProbabilityIsLessThan(double probability) {
        return individuals.stream().filter(individual -> individual.getProbability() >= probability)
                .findAny().orElseThrow();
    }

    public Individual findIndividualByProbabilityIsLessThan(double probability, Individual ind) {
        return individuals.stream().filter(individual -> individual.getProbability() >= probability && individual != ind)
                .findAny().orElseThrow();
    }

    public Individual mostPrepared() {
        double maxFitness = individuals.stream().mapToDouble(Individual::getFitness).max().orElseThrow();

        return individuals.stream().filter(individual -> individual.getFitness() == maxFitness).findAny().orElseThrow();
    }

    @Override
    public void run() {
        computeIndividualMetrics();
    }
}
