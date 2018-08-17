package org.iconic;

import com.beust.jcommander.JCommander;
import lombok.extern.log4j.Log4j2;
import org.iconic.ea.EvolutionaryAlgorithm;
import org.iconic.ea.chromosome.Chromosome;
import org.iconic.ea.chromosome.cartesian.CartesianChromosome;
import org.iconic.ea.chromosome.cartesian.CartesianChromosomeFactory;
import org.iconic.ea.data.DataManager;
import org.iconic.ea.operator.evolutionary.mutation.cgp.CartesianSingleActiveMutator;
import org.iconic.ea.operator.objective.DefaultObjective;
import org.iconic.ea.operator.objective.error.MeanSquaredError;
import org.iconic.ea.operator.primitive.*;
import org.iconic.ea.strategies.cgp.CartesianGeneticProgramming;
import org.iconic.io.ArgsConverterFactory;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

@Log4j2
public class Client {
    private final JCommander argParser;
    private final ArgsConverterFactory args;

    public static void main(String[] args) {
        // Create a new client and parse the arguments passed in to the program
        final Client client = new Client();
        client.parse(args);

        // Check if the user included the help flag
        if (client.getArgs().isHelp()) {
            client.usage();
        }

        // Check if the user passed in an input file
        final String inputFile = client.getArgs().getInput();

        if (!"".equals(inputFile) && !inputFile.isEmpty()) {
            final DataManager<Double> dm = new DataManager<>(inputFile);

            int featureSize = dm.getFeatureSize();
            int sampleSize = dm.getSampleSize();

            log.info("Feature Size: {}", () -> featureSize - 1);
            log.info("Sample Size: {}", () -> sampleSize);

            // Create a supplier for Gene Expression Programming chromosomes
            CartesianChromosomeFactory<Double> supplier = new CartesianChromosomeFactory<>(
                    1, featureSize - 1, 5, 5, 5
            );

            // Add in the functions the chromosomes can use
            supplier.addFunction(Arrays.asList(
                    new Addition(), new Subtraction(), new Multiplication(), new Division(),
                    new Power(), new Root(), new Sin(), new Cos(), new Tan()
            ));

            // Create an evolutionary algorithm using Gene Expression Programming
            EvolutionaryAlgorithm<CartesianChromosome<Double>, Double> ea =
                    new CartesianGeneticProgramming<>(supplier);
            ea.setCrossoverProbability(client.getArgs().getCrossoverProbability());
            ea.setMutationProbability(client.getArgs().getMutationProbability());

            // Add in the evolutionary operators the algorithm can use
            ea.addMutator(new CartesianSingleActiveMutator<>());

            // Add in the objectives the algorithm should aim for
            ea.addObjective(
                    new DefaultObjective<>(
                            new MeanSquaredError(), dm
            ));

//            log.info("Function Primitives used: {}", supplier::getFunctions);

            final Comparator<Chromosome<Double>> comparator = Comparator.comparing(Chromosome::getFitness);
            ea.initialisePopulation(client.getArgs().getPopulation(), dm.getFeatureSize());
            List<CartesianChromosome<Double>> population = ea.getChromosomes();

            for (int i = 0; i < client.getArgs().getGenerations(); ++i) {
                ea.evolve(population);

                Chromosome<Double> bestCandidate = population
                        .stream().min(comparator).get();

                log.info("\n\tGeneration: {}\n\tBest candidate: {}\n\tFitness: {}",
                        i + 1, bestCandidate.toString(), bestCandidate.getFitness());
            }
        }
    }

    private Client() {
        this.args = new ArgsConverterFactory();
        this.argParser = new JCommander.Builder().programName("Iconic CLI").addObject(this.args).build();
    }

    private void parse(final String[] args) {
        getArgParser().parse(args);
    }

    private void usage() {
        getArgParser().usage();
    }

    private JCommander getArgParser() {
        return argParser;
    }

    private ArgsConverterFactory getArgs() {
        return args;
    }
}