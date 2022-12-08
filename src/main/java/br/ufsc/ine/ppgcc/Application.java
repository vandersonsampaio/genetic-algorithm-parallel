package br.ufsc.ine.ppgcc;

import br.ufsc.ine.ppgcc.utils.GeneticAlgorithm;
import org.springframework.boot.Banner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.concurrent.ExecutionException;

import static java.lang.System.exit;

@SpringBootApplication
public class Application implements CommandLineRunner {

    private final GeneticAlgorithm ga;

    public Application(GeneticAlgorithm ga) {
        this.ga = ga;
    }

    public static void main(String... args) {
        SpringApplication app = new SpringApplication(Application.class);
        app.setBannerMode(Banner.Mode.OFF);
        app.run(args);
    }

    @Override
    public void run(String... args) throws ExecutionException, InterruptedException {
        ga.compute();
        exit(0);
    }
}
