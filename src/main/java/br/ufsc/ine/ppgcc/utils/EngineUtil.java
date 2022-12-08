package br.ufsc.ine.ppgcc.utils;

import br.ufsc.ine.ppgcc.population.Individual;
import br.ufsc.ine.ppgcc.population.Weight;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.math.BigDecimal;
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

    @Value("${ga-parallel.input.external-metrics}")
    private String inputExternalMetrics;

    @Value("${ga-parallel.input.computed-metrics}")
    private String inputComputedMetrics;

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
        return csvUtil.readFile(inputComputedMetrics);
    }

    private double[] loadExternalMetrics() throws IOException {
        double[][] data = csvUtil.readFile(inputExternalMetrics);
        double[] result = new double[data.length];

        IntStream.range(0, data.length).forEach(index -> result[index] = data[index][0]);
        return result;
    }

    public Individual generateIndividual(int lengthWeight) {
        Weight[] weights = new Weight[lengthWeight];

        IntStream.range(0, lengthWeight).forEach(index -> {
            double value = random.nextDouble();
            int signal = random.nextInt(2);

            double weightValue = value * (signal == 0 ? 1 : -1);
            weights[index] = new Weight(BigDecimal.valueOf(weightValue));
        });

        return new Individual(weights);
    }

    public double[] computeMetric(Individual individual) {
        double[] dataComputed = new double[metrics.length];

        for(int i = 0; i < metrics.length; i++){
            for(int j = 0; j < metrics[0].length; j++)
                dataComputed[i] += individual.getValues()[j].getValue().doubleValue() * metrics[i][j];
        }

        return dataComputed;
    }

    public double computeCoefficient(double[] data) {
        return Math.abs(pearsonCorrelation.correlation(data, externalMetrics));
    }
}
