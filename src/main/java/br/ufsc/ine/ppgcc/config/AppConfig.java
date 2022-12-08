package br.ufsc.ine.ppgcc.config;

import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;
import org.springframework.beans.factory.annotation.Value;
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

    @Bean("fitness_executors")
    public ExecutorService fitnessExecutor(@Value("${ga-parallel.fitness.executor.pool.number}") int pool) {
        return Executors.newFixedThreadPool(pool);
    }

    @Bean("select_executors")
    public ExecutorService selectExecutor(@Value("${ga-parallel.select.executor.pool.number}") int pool) {
        return Executors.newFixedThreadPool(pool);
    }

    @Bean("number_fitness_partitions")
    public Integer numberFitnessPartitions(@Value("${ga-parallel.fitness.executor.pool.number}") int partitions) {
        return partitions;
    }

    @Bean("number_select_partitions")
    public Integer numberSelectPartitions(@Value("${ga-parallel.select.executor.pool.number}") int partitions) {
        return partitions;
    }
}
