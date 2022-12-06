package br.ufsc.ine.ppgcc;

import br.ufsc.ine.ppgcc.utils.GeneticAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import static java.lang.System.exit;

@SpringBootApplication
public class Application implements CommandLineRunner {

    @Autowired
    private GeneticAlgorithm ga;

    public static void main(String... args) {
        SpringApplication app = new SpringApplication(Application.class);
        app.setBannerMode(Banner.Mode.OFF);
        app.run(args);
    }

    @Override
    public void run(String... args) {
        int maxGenerations = 200;

        ga.compute(maxGenerations);
        exit(0);
    }
}
