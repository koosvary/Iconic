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

import java.util.*;

@Log4j2
public class Client {
    /** Parses an array of strings as tokens to be converted by a provided factory */
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

        // Don't do anything if they didn't
        if (inputFile != null && !inputFile.isEmpty()) {
            final DataManager<Double> dm = new DataManager<>(inputFile);

            // Collect all relevant parameters for convenience
            int featureSize = dm.getFeatureSize();
            int sampleSize = dm.getSampleSize();
            int outputs = client.getArgs().getOutputs();
            int columns = client.getArgs().getColumns();
            int rows = client.getArgs().getRows();
            int levelsBack = client.getArgs().getLevelsBack();

            log.info("Feature Size: {}", () -> featureSize - 1);
            log.info("Sample Size: {}", () -> sampleSize);

            // Create a supplier for Gene Expression Programming chromosomes
            CartesianChromosomeFactory<Double> supplier = new CartesianChromosomeFactory<>(
                    outputs, featureSize - 1, columns, rows, levelsBack
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

            final int generations = client.getArgs().getGenerations();
            final Comparator<Chromosome<Double>> comparator = Comparator.comparing(Chromosome::getFitness);
            ea.initialisePopulation(client.getArgs().getPopulation());
            List<CartesianChromosome<Double>> population = ea.getChromosomes();
            Chromosome<Double> bestCandidate = population.stream().min(comparator).get();
            int currentGen = 0;
            long time = System.currentTimeMillis();
            long oldTime = System.currentTimeMillis();
            // Start the evolutionary loop
            for (int i = 0; i < generations; ++i) {
                final int percent = intToPercent(i, generations);

                population = ea.evolve(population);
                // Retrieve the individual with the best fitness
                bestCandidate = population.stream().min(comparator).get();

                // Pretty-print a summarised progress indicator
                StringBuilder out = new StringBuilder();

                if(i == 0)
                    time = System.currentTimeMillis()-time;
                out.append("\r")
                        .append("Progress: ").append(percent).append("%")
                        // And include the current best fitness
                        .append("\t|\tFitness: ").append(bestCandidate.getFitness())
                        .append("\t|\tGeneration: ").append(currentGen)
                        .append("\t|\tTime: ").append(time + " ");
                if(i%100 == 0){
                    currentGen = i;
                    time += System.currentTimeMillis()-oldTime;
                    oldTime = System.currentTimeMillis();
                }
                System.out.print(out);
            }

            // When it ends print out the actual genome of the best candidate
            log.info("\n\tBest candidate: {}\n\tFitness: {}",
                    bestCandidate.toString(), bestCandidate.getFitness()
            );
            System.out.println("y = " +((CartesianChromosome<Double>) bestCandidate).getExpression());
            System.out.println(((CartesianChromosome<Double>) bestCandidate).getResults());
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

    /**
     * <p>Return the ArgsConverterFactory of this Client</p>
     *
     * @return the ArgsConverterFactory of the client
     */
    private ArgsConverterFactory getArgs() {
        return args;
    }

    /**
     * <p>Converts the provided progress into a percentage of the provided total</p>
     *
     * @param progress the current progress
     * @param total    the total
     * @return the current progress as a percentage of the total
     */
    private static int intToPercent(final int progress, final int total) {
        return (progress * 100) / total;
    }
}