package org.iconic;

import com.beust.jcommander.JCommander;
import lombok.extern.log4j.Log4j2;
import org.iconic.ea.chromosome.Chromosome;
import org.iconic.ea.chromosome.ExpressionChromosome;
import org.iconic.ea.data.DataManager;
import org.iconic.ea.gep.GeneExpressionProgramming;
import org.iconic.ea.operator.evolutionary.crossover.gep.SimpleExpressionCrossover;
import org.iconic.ea.operator.evolutionary.mutation.gep.ExpressionMutator;
import org.iconic.ea.operator.objective.DefaultObjective;
import org.iconic.ea.operator.objective.error.MeanSquaredError;
import org.iconic.ea.operator.primitive.*;
import org.iconic.io.ArgsConverterFactory;

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
            final DataManager<Double> dm = new DataManager<>(Double.class, inputFile);

            int featureSize = dm.getFeatureSize();
            int sampleSize = dm.getSampleSize();

            log.info("Feature Size: {}", () -> featureSize);
            log.info("Sample Size: {}", () -> sampleSize);

            // Create an evolutionary algorithm using Gene Expression Programming
            GeneExpressionProgramming<Double> gep = new GeneExpressionProgramming<>();
            gep.setCrossoverProbability(client.getArgs().getCrossoverProbability());
            gep.setMutationProbability(client.getArgs().getMutationProbability());

            // Add in the functions it can use
            gep.addFunction(new Addition());
            gep.addFunction(new Subtraction());
            gep.addFunction(new Multiplication());
            gep.addFunction(new Division());
            gep.addFunction(new Power());
            gep.addFunction(new Root());
            gep.addFunction(new Sin());
            gep.addFunction(new Cos());
            gep.addFunction(new Tan());

            // Add in the evolutionary operators it can use
            gep.addCrossover(new SimpleExpressionCrossover<>());
            gep.addMutator(new ExpressionMutator<>());

            // Add in the objectives it should aim for
            gep.addObjective(
                    new DefaultObjective<>(
                            new MeanSquaredError(), dm.getSamples())
            );

//            log.info("Function Primitives used: {}", gep::getFunctions);

            final Comparator<Chromosome<Double>> comparator = Comparator.comparing(Chromosome::getFitness);
            gep.initialisePopulation(client.getArgs().getPopulation(), dm.getFeatureSize());
            List<ExpressionChromosome<Double>> population = gep.getChromosomes();

            for (int i = 0; i < client.getArgs().getGenerations(); ++i) {
                gep.evolve(population);

                ExpressionChromosome<Double> bestCandidate = population
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