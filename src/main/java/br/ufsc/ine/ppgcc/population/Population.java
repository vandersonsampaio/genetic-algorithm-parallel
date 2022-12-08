package br.ufsc.ine.ppgcc.population;

import br.ufsc.ine.ppgcc.debug.Info;
import br.ufsc.ine.ppgcc.utils.EngineUtil;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

@Component
public class Population {

    @Value("${ga-parallel.input.weight.number}")
    private Integer numberWeights;
    private final int numberFitnessPartitions;
    private final int numberSelectPartitions;
    private final EngineUtil engineUtil;
    @Getter
    private final Integer populationSize;
    @Getter
    private List<Individual> individuals;

    private final List<List<Individual>> individualsFitnessSplited;
    private final List<List<Individual>> individualsSelectSplited;

    private List<Individual> sons;

    public Population(EngineUtil engineUtil, @Value("${ga-parallel.population.size}") Integer populationSize,
                      @Qualifier("number_fitness_partitions") int numberFitnessPartitions,
                      @Qualifier("number_select_partitions") int numberSelectPartitions) {
        this.engineUtil = engineUtil;
        this.populationSize = populationSize;
        this.numberFitnessPartitions = numberFitnessPartitions;
        this.numberSelectPartitions = numberSelectPartitions;
        individuals = new ArrayList<>();
        sons = new ArrayList<>();
        individualsFitnessSplited = new ArrayList<>();
        individualsSelectSplited = new ArrayList<>();
    }

    public void startPopulation() {
        IntStream.range(0, populationSize)
                .forEach(index -> individuals.add(engineUtil.generateIndividual(numberWeights)));

        splitIndividuals(individualsFitnessSplited, numberFitnessPartitions);
        splitIndividuals(individualsSelectSplited, numberSelectPartitions);
    }

    private void splitIndividuals(List<List<Individual>> list, int numberPartitions) {
        list.clear();
        int divisor = populationSize / numberPartitions;

        IntStream.range(0, numberPartitions)
                .forEach(i ->
                        list.add(individuals.subList(i * divisor, i * divisor + divisor)));
    }

    public long computeIndividualMetrics(int partition) {
        Info info = new Info();

        computeFitness(partition);
        probabilities(partition);

        info.finishCount();
        return info.totalTime();
    }

    private void computeFitness(int position) {
        List<Individual> individualPart = individualsFitnessSplited.get(position);
        IntStream.range(0, individualPart.size()).forEach(index -> {
            double[] computeData = engineUtil.computeMetric(individualPart.get(index));
            individualPart.get(index).setFitness(engineUtil.computeCoefficient(computeData));
        });
    }

    private void probabilities(int partition) {
        List<Individual> individuals = individualsFitnessSplited.get(partition);
        individuals.sort(Comparator.comparing(Individual::getFitness));

        double criteriaTotal = individuals
                .stream().mapToDouble(Individual::getFitness).sum();

        double range = 0D;
        for (Individual individual : individuals) {
            double aux = individual.getFitness() / criteriaTotal;
            range += aux;
            individual.setProbability(range);
        }

        individuals.get(individuals.size() - 1).setProbability(1);
    }

    public void evolveGeneration() {
        individuals = sons;

        splitIndividuals(individualsFitnessSplited, numberFitnessPartitions);
        splitIndividuals(individualsSelectSplited, numberSelectPartitions);
    }

    public void cleanSons() {
        sons = new ArrayList<>();
    }

    public synchronized void addSon(List<Individual> individual) {
        sons.addAll(individual);
    }

    public Individual findIndividualByProbabilityIsLessThan(int partition, double probability) {
        List<Individual> list = individualsSelectSplited.get(partition);
        int low = 0;
        int high = list.size();
        int mid;

        while(low <= high) {
            mid=(low + high) / 2;

            if(list.get(mid).getProbability() < probability) {
                low = mid + 1;
            } else {
                return list.get(mid);
            }
        }

        return Optional.<Individual>empty().orElseThrow();
    }

    public Individual mostPrepared() {
        double maxFitness = individuals.stream().mapToDouble(Individual::getFitness).max().orElseThrow();

        return individuals.stream().filter(individual -> individual.getFitness() == maxFitness).findAny().orElseThrow();
    }
}
