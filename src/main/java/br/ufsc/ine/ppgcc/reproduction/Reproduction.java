package br.ufsc.ine.ppgcc.reproduction;

import br.ufsc.ine.ppgcc.population.Individual;
import br.ufsc.ine.ppgcc.population.Parents;
import br.ufsc.ine.ppgcc.population.Weight;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Random;

/**
 * Classe responsável por realizar o Cruzamento e as eventuais Mutações durante uma Reprodução de Indivíduos
 */
@Component
@RequiredArgsConstructor
public class Reproduction {

    @Value("${ga-parallel.reproduction.mutation-rate}")
    private double mutationRate;
    private final Random random;

    /**
     * Método que a partir de Dois Indivíduos (Parents) retorna novos dois Indivíduos, cada um com parte do Gene
     * de um Pai e parte do Gene do outro Pai.
     * O método gera dois pontos de cortes distintos para a geração dos novos dois filhos
     * @param parents Pais selecionado
     * @return Lista com dois novos Indivíduos
     */
    public List<Individual> crossover(Parents parents) {
        int lengthChromosome = parents.getParent(Boolean.TRUE).getValues()[0].getChromosome().length;
        int[] chromosomeSonOne = new int[lengthChromosome];
        int[] chromosomeSonTwo = new int[lengthChromosome];
        int length = parents.getParent(Boolean.TRUE).getValues().length;
        Weight[] weightSonOne = new Weight[length];
        Weight[] weightSonTwo = new Weight[length];

        for (int i = 0; i < length; i++) {
            int cutoffOne = random.nextInt(lengthChromosome);
            int cutoffTwo = random.nextInt(lengthChromosome);
            int zero = random.nextInt(2);

            Individual parentOne = parents.getParent(zero == 0);
            Individual parentTwo = parents.getParent(zero == 1);

            Weight valueOne = parentOne.getValues()[i];
            Weight valueTwo = parentTwo.getValues()[i];

            for (int j = 0; j < cutoffOne; j++) {
                chromosomeSonOne[j] = valueOne.getChromosome()[j];
            }

            for (int j = 0; j < cutoffTwo; j++) {
                chromosomeSonTwo[j] = valueTwo.getChromosome()[j];
            }

            for (int j = cutoffOne; j < lengthChromosome; j++) {
                chromosomeSonOne[j] = valueTwo.getChromosome()[j];
            }

            for (int j = cutoffTwo; j < lengthChromosome; j++) {
                chromosomeSonTwo[j] = valueOne.getChromosome()[j];
            }

            weightSonOne[i] = new Weight(mutation(chromosomeSonOne));
            weightSonTwo[i] = new Weight(mutation(chromosomeSonTwo));
        }

        return List.of(new Individual(weightSonOne), new Individual(weightSonTwo));
    }

    /**
     * Método de Mutação, a partir de uma taxa (Mutation Rate).
     * A mutação pode ocorrer em algum gene do cromossomo, podendo ser o gene do sinal (+/-) ou em
     * algum gene do valor numérico
     * @param chromosome Cromossomo submetido a verificação de mutação
     * @return vetor cromossômico com mutação ou não
     */
    private int[] mutation(int[] chromosome) {
        double rate = random.nextDouble();
        if (rate <= mutationRate) {
            int signal = random.nextInt(2);
            int i = random.nextInt(chromosome.length - 2) + 2;
            int value = random.nextInt(2);

            chromosome[1] = signal;
            chromosome[i] = value;
        }

        return chromosome;
    }
}
