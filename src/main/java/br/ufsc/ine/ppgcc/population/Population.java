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

/**
 * Classe que realiza a gestão de toda uma População
 */
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

    //Listas adicionais para armazenar a lista de indivíduos divida de acordo com o tipo (Fitness ou Select)
    //e a quantidade de particões de cada tipo
    private final List<List<Individual>> individualsFitnessSplitted;
    private final List<List<Individual>> individualsSelectSplitted;

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
        individualsFitnessSplitted = new ArrayList<>();
        individualsSelectSplitted = new ArrayList<>();
    }

    /**
     * Método responsável por popular a Lista de Invidívuos e as Listas Divididas (splitted)
     */
    public void startPopulation() {
        IntStream.range(0, populationSize)
                .forEach(index -> individuals.add(engineUtil.generateIndividual(numberWeights)));

        splitIndividuals(individualsFitnessSplitted, numberFitnessPartitions);
        splitIndividuals(individualsSelectSplitted, numberSelectPartitions);
    }

    private void splitIndividuals(List<List<Individual>> list, int numberPartitions) {
        list.clear();
        int divisor = populationSize / numberPartitions;

        IntStream.range(0, numberPartitions)
                .forEach(i ->
                        list.add(individuals.subList(i * divisor, i * divisor + divisor)));
    }

    /**
     * Método responsável por computar o fitness de todos os indivíduos da população
     * @param position número da partição que será calculado
     * @return tempo total gasto para executar a função
     */
    public long computeFitness(int position) {
        Info info = new Info();

        List<Individual> individualPart = individualsFitnessSplitted.get(position);
        IntStream.range(0, individualPart.size()).forEach(index -> {
            double[] computeData = engineUtil.computeMetric(individualPart.get(index));
            individualPart.get(index).setFitness(engineUtil.computeCoefficient(computeData));
        });

        info.finishCount();
        return info.totalTime();
    }

    /**
     * Método responsável por gerar valores de probabilidade (entre 0 e 1) de seleção de um indivíduo a
     * partir de seu Fitness
     * @param partition número da partição que terá sua probabilidade calculada
     */
    public void probabilities(int partition) {
        List<Individual> individualsSplitted = individualsSelectSplitted.get(partition);
        individualsSplitted.sort(Comparator.comparing(Individual::getFitness));

        double criteriaTotal = individualsSplitted
                .stream().mapToDouble(Individual::getFitness).sum();

        double range = 0D;
        for (Individual individual : individualsSplitted) {
            double aux = individual.getFitness() / criteriaTotal;
            range += aux;
            individual.setProbability(range);
        }

        individualsSplitted.get(individualsSplitted.size() - 1).setProbability(1);
    }

    /**
     * Método que promove as gerações da população
     * Também responsável por dividir as partições da nova população
     */
    public void evolveGeneration() {
        individuals = sons;

        splitIndividuals(individualsFitnessSplitted, numberFitnessPartitions);
        splitIndividuals(individualsSelectSplitted, numberSelectPartitions);
    }

    public void cleanSons() {
        sons = new ArrayList<>();
    }

    /**
     * Método sincronizado que inserção do indivíduo filho na lista de filhos
     * @param individual Novo indivíduo gerado a partir do cruzamento de outros indivíduos
     */
    public synchronized void addSon(List<Individual> individual) {
        sons.addAll(individual);
    }

    /**
     * Método de busca binária em Lista Ordenada para encontrar um indivíduo com probabilidade maior ou igual
     * a probabilidade informada
     * @param partition número da partição que será realizado a busca
     * @param probability probabilidade mínima necessária para a seleção
     * @return Indivíduo que corresponde aos critérios de seleção
     */
    public Individual findIndividualByProbabilityIsLessThan(int partition, double probability) {
        List<Individual> list = individualsSelectSplitted.get(partition);
        int low = 0;
        int high = list.size();
        int middle;

        while(low <= high) {
            middle =(low + high) / 2;

            if(list.get(middle).getProbability() < probability) {
                low = middle + 1;
            } else {
                return list.get(middle);
            }
        }

        return Optional.<Individual>empty().orElseThrow();
    }

    /**
     * Método que retorna o individuo com maior Fitness de uma população
     * @return Melhor Indivíduo da população
     */
    public Individual mostPrepared() {
        double maxFitness = individuals.stream().mapToDouble(Individual::getFitness).max().orElseThrow();

        return individuals.stream().filter(individual -> individual.getFitness() == maxFitness).findAny().orElseThrow();
    }
}
