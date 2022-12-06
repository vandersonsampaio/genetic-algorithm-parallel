package br.ufsc.ine.ppgcc.config;

import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class AppConfig {

    @Bean
    public Random commonRandom() {
        return new Random();
    }

    @Bean
    public PearsonsCorrelation pearsonCorrelation() {
        return new PearsonsCorrelation();
    }

    @Bean("population_size")
    public Integer populationSize() {
        return 20000;
    }

    @Bean("fitness_executors")
    public ExecutorService fitnessExecutor() {
        return Executors.newFixedThreadPool(4);
    }

    @Bean("select_executors")
    public ExecutorService selectExecutor() {
        return Executors.newFixedThreadPool(1);
    }

    @Bean("number_partitions")
    public Integer numberPartitions() {
        return 2;
    }
}
