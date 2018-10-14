/**
 * Copyright (C) 2018 Iconic
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.iconic;

import com.beust.jcommander.JCommander;
import lombok.extern.log4j.Log4j2;
import org.iconic.ea.EvolutionaryAlgorithm;
import org.iconic.ea.chromosome.Chromosome;
import org.iconic.ea.chromosome.ChromosomeFactory;
import org.iconic.ea.chromosome.cartesian.CartesianChromosome;
import org.iconic.ea.chromosome.cartesian.CartesianChromosomeFactory;
import org.iconic.ea.data.DataManager;
import org.iconic.ea.data.FeatureClass;
import org.iconic.ea.data.preprocessing.HandleMissingValues;
import org.iconic.ea.operator.evolutionary.mutation.cgp.CartesianSingleActiveMutator;
import org.iconic.ea.operator.objective.*;
import org.iconic.ea.operator.objective.error.MeanSquaredError;
import org.iconic.ea.operator.objective.multiobjective.SimpleMultiObjective;
import org.iconic.ea.operator.primitive.*;
import org.iconic.ea.strategies.seamo.ElitistSEAMO;
import org.iconic.ea.strategies.seamo.SEAMO;
import org.iconic.io.ArgsConverterFactory;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

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

            // Sanatise the dataset for any missing values
            handleMissingValues(dm);

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
            ChromosomeFactory<CartesianChromosome<Double>, Double> supplier = new CartesianChromosomeFactory<>(
                    outputs, featureSize - 1, columns, rows, levelsBack
            );

            // Add in the functions the chromosomes can use
            supplier.addFunction(Arrays.asList(
                    new Addition(), new Subtraction(), new Multiplication(), new Division(),
                    new Power(), new Root(), new Sin(), new Cos(), new Tan()
            ));

            // Create an evolutionary algorithm using Gene Expression Programming
            EvolutionaryAlgorithm<CartesianChromosome<Double>, Double> ea =
                    new SEAMO<>(supplier, 1);
            ea.setCrossoverProbability(client.getArgs().getCrossoverProbability());
            ea.setMutationProbability(client.getArgs().getMutationProbability());

            // Add in the evolutionary operators the algorithm can use
            ea.addMutator(new CartesianSingleActiveMutator<>());

            // Add in the objectives the algorithm should aim for
            ea.setObjective(
//                    new CacheableObjective<>(
                        new SimpleMultiObjective(
                            Arrays.asList(
                                    new CacheableObjective<>(
                                            new DefaultObjective(new MeanSquaredError(), dm)
                                    ),
                                    new CacheableObjective<>(
                                            new SizeObjective()
                                    )
                            )
                    )
            );

            final int generations = client.getArgs().getGenerations();
            final Comparator<Chromosome<Double>> comparator = Comparator.comparing(Chromosome::getFitness);
            ea.initialisePopulation(client.getArgs().getPopulation());

            List<CartesianChromosome<Double>> population = ea.getChromosomes();
//            Chromosome<Double> bestCandidate = population.stream().min(comparator).get();

            Instant start = Instant.now();
            int percent;
            // Pretty-print a summarised progress indicator
            StringBuilder out = new StringBuilder();
            // Start the evolutionary loop
            for (int i = 0; i < generations; ++i) {


                population = ea.evolve(population);
                // Retrieve the individual with the best fitness
//                bestCandidate = population.stream().min(comparator).get();
                percent = intToPercent(i, generations);

                String global = (ea instanceof SEAMO)
                        ? " (" + ((SEAMO<?, Double>) ea).getGlobals().values().stream()
                        .map(v -> String.format("%.4f", v)).collect(Collectors.joining(", ")) + ")"
                        : "";

                // And include the current best fitness
                out.append("\r")
                        .append("Progress: ").append(percent).append("%")
                        // And include the current best fitness
                        .append("\t|\tGlobal Bests: ").append(global)
                        .append("\t|\tGeneration: ").append(i + 1)
                        .append("\t|\tTime: ").append(Duration.between(start, Instant.now())).append(" ");
                System.out.print(out);
            }
            // When it ends print out the actual genome of the best candidate
//            log.info("\n\tBest candidate: {}\n\tFitness: {}",
//                    bestCandidate.toString(), String.format("%.4f", bestCandidate.getFitness())
//            );
//            System.out.println("y = " + bestCandidate.simplifyExpression(bestCandidate.getExpression(
//                    bestCandidate.toString(), new ArrayList<>(supplier.getFunctionalPrimitives()),true)));
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

    /**
     * Sanitises the dataset for any missing values, if any missing values occur in the columns they will be replaced
     * with the value '1'.
     */
    private static void handleMissingValues(DataManager<Double> dm) {
        // Get the dataset Feature classes
        HashMap<String, FeatureClass<Number>> dataset = dm.getDataset();

        // Check each feature class to see if any column is missing values
        for (HashMap.Entry<String, FeatureClass<Number>> entry : dataset.entrySet()) {
            String key = entry.getKey();
            FeatureClass<Number> featureClass = entry.getValue();

            // If the column is missing values - apply the HandleMissingValues pre-processing and change all null
            // values to a '1'
            if (featureClass.isMissingValues()) {
                log.info("Dataset is missing values in column '" + key + "' replacing those values with the value '1'");

                // Create the handle missing values pre-processing object and set its type
                HandleMissingValues handleMissingValues = new HandleMissingValues();
                handleMissingValues.setMode(HandleMissingValues.Mode.ONE);

                featureClass.addPreprocessor(handleMissingValues);
            }
        }
    }
}