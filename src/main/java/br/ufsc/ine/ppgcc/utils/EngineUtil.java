package br.ufsc.ine.ppgcc.utils;

import br.ufsc.ine.ppgcc.population.Individual;
import br.ufsc.ine.ppgcc.population.Weight;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Random;
import java.util.stream.IntStream;

@RequiredArgsConstructor
@Component
public class EngineUtil {

    private final PearsonsCorrelation pearsonCorrelation;
    private final CsvUtil csvUtil;
    private final Random random;
    private double[][] metrics;
    private double[] externalMetrics;

    @PostConstruct
    public void init() {
        try {
            metrics = loadComputedMetrics();
            externalMetrics = loadExternalMetrics();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private double[][] loadComputedMetrics() throws IOException {
        return csvUtil.readFile(new ClassPathResource("input/computed_metrics.csv")
                .getFile().getAbsolutePath());
    }

    private double[] loadExternalMetrics() throws IOException {
        double[][] data = csvUtil.readFile(new ClassPathResource("input/external_metrics.csv")
                .getFile().getAbsolutePath());
        double[] result = new double[data.length];

        IntStream.range(0, data.length).forEach(index -> result[index] = data[index][0]);
        return result;
    }

    public Individual generateIndividual(int lengthWeight) {
        Weight[] weights = new Weight[lengthWeight];

        IntStream.range(0, lengthWeight).forEach(index -> {
            double value = random.nextDouble();
            int numberIntegers = random.nextInt(4);
            int signal = random.nextInt(2);

            double weightValue = value * Math.pow(10, numberIntegers) * (signal == 0 ? 1 : -1);
            weights[index] = new Weight(weightValue);
        });

        return new Individual(weights);
    }

    public double[] computeMetric(Individual individual) {
        double[] dataComputed = new double[metrics.length];

        for(int i = 0; i < metrics.length; i++){
            for(int j = 0; j < metrics[0].length; j++)
                dataComputed[i] += individual.getValues()[j].getValue() * metrics[i][j];
        }

        return dataComputed;
    }

    public double computeCoefficient(double[] data) {
        return Math.abs(pearsonCorrelation.correlation(data, externalMetrics));
    }
}
