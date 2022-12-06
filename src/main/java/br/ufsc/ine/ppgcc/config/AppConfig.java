package br.ufsc.ine.ppgcc.config;

import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;

import java.util.Random;

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

    @Bean
    public Integer populationSize() {
        return 20000;
    }

    @Bean
    public TaskExecutor taskExecutor() {
        return new SimpleAsyncTaskExecutor();
    }
}
