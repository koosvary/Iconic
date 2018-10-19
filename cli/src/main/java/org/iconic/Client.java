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
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
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
import org.iconic.ea.strategies.seamo.SEAMO;
import org.iconic.io.cli.ArgsConverterFactory;
import org.iconic.utils.GraphWriter;
import org.iconic.utils.XYGraphWriter;
import org.knowm.xchart.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Log4j2
public class Client {
    /**
     * Parses an array of strings as tokens to be converted by a provided factory
     */
    private final JCommander argParser;
    private final ArgsConverterFactory args;
    private static final long NOW = Instant.now().getEpochSecond();

    public static void main(String[] args) {
        // Create a new client and parse the arguments passed in to the program
        final Client client = new Client();
        client.parse(args);

        // Check if the user passed in the help flag
        if (client.getArgs().isHelp()) {
            client.usage();
        }

        // Check if the user passed in an input file
        final String inputFile = client.getArgs().getInput();
        final String[] fileNameParts = inputFile.split(Pattern.quote("."));
        final String fileName = fileNameParts[fileNameParts.length - 2];

        // Don't do anything if they didn't pass in an input file
        if (!inputFile.isEmpty()) {
            final DataManager<Double> dm = new DataManager<>(inputFile);

            // Sanitise the dataset for any missing values
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

            // Create a supplier for chromosomes
            ChromosomeFactory<CartesianChromosome<Double>, Double> supplier = new CartesianChromosomeFactory<>(
                    outputs, featureSize - 1, columns, rows, levelsBack
            );

            // Add all of the functions the chromosomes can use
            supplier.addFunction(Arrays.asList(
                    new Addition(), new Subtraction(), new Multiplication(), new Division(),
                    new Ceiling(), new Exponential(), new GaussianFunction(),
                    new Power(), new Sin()
            ));

            Set<Chromosome<Double>> nonDominatedFinal = new LinkedHashSet<>();
            Set<Chromosome<Double>> nonDominatedAll = new LinkedHashSet<>();

            final Instant start = Instant.now();

            // Start the evolutionary loop
            for (int trial = 0; trial < client.getArgs().getRepetitions(); ++trial) {
                EvolutionaryAlgorithm<CartesianChromosome<Double>, Double> ea = getEvolutionaryAlgorithm(
                        client.getArgs(), dm, supplier
                );

                // Initialise the population
                final int generations = client.getArgs().getGenerations();
                ea.initialisePopulation(client.getArgs().getPopulation());
                List<CartesianChromosome<Double>> population = ea.getChromosomes();

                for (int i = 0; i < generations; ++i) {
                    population = ea.evolve(population);
                    // Pretty-print a summarised progress indicator
                    printOutput(ea, generations, start, i, trial);
                }

                nonDominatedFinal.addAll(
                        ((SEAMO<CartesianChromosome<Double>, Double>) ea)
                                .getGlobalChromosomes().values()
                );
                nonDominatedAll.addAll(
                        ((SEAMO<CartesianChromosome<Double>, Double>) ea)
                                .getArchive()
                );
            }

            final String directory = fileName + "//" + NOW;

            // Make sure the output directory exists
            try {
                Files.createDirectories(Paths.get(directory));
                // Export the CLI arguments to a README file
                writeReadme(client.getArgs(), directory, Duration.between(start, Instant.now()));
                // Export the results to a CSV file
                if (client.getArgs().isCsv()) {
                    exportCsv(directory, "results-last-gen", nonDominatedFinal);
                    exportCsv(directory, "results-all-gen", nonDominatedAll);
                }
                // Print and export a graph of the solutions plotted by their dimensions
                if (client.getArgs().isGraph()) {
                    GraphWriter writer = new XYGraphWriter(
                            "Mean Squared Error", "Size", "Plot of Non-Dominated Solution",
                            XYSeries.XYSeriesRenderStyle.Scatter, Chromosome::getFitness, Chromosome::getSize
                    );

                    drawGraph(nonDominatedFinal, writer);
                    writer.export("Last Generation - Non-Dominated", directory, "results-last");
                    writer.clear();

                    drawGraph(nonDominatedAll, writer);
                    writer.export("Last Generation - Non-Dominated", directory, "results-all");
                }
            } catch (IOException ex) {
                log.error("{}: {}", ex::getMessage, ex::getCause);
            }
        }
    }

    /**
     * Writes a README to the user's filesystem using the arguments provided.
     *
     * @param args      The argument factory whose values are to be include in the README.
     * @param directory The directory to write the README to.
     * @param time      The time to record.
     */
    private static void writeReadme(final ArgsConverterFactory args, final String directory, final Duration time)
            throws IOException {
        try (FileWriter printer = new FileWriter(new File(directory + "//" + "README"))) {
            printer.write(String.format("Input File:\t\t%s\n", args.getInput()));
            printer.write(String.format("Eval. Time:\t\t%s\n", time.toString()));
            printer.write(String.format("Repetitions:\t%s\n", args.getRepetitions()));
            printer.write(String.format("Population:\t\t%s\n", args.getPopulation()));
            printer.write(String.format("Generations:\t%s\n", args.getGenerations()));
            printer.write(String.format("Outputs:\t\t\t%s\n", args.getOutputs()));
            printer.write(String.format("Rows:\t\t\t\t\t%s\n", args.getRows()));
            printer.write(String.format("Columns:\t\t\t%s\n", args.getColumns()));
            printer.write(String.format("Levels Back:\t%s\n", args.getLevelsBack()));
        }
    }

    /**
     * Exports a provided population of chromosomes to a CSV file with the specified directory and name.
     * <p>
     * The exported file uses an Excel-compatible CSV format.
     *
     * @param directory  The directory to write the CSV file to.
     * @param fileName   The name of the file to write.
     * @param population The population to write to the file.
     */
    private static void exportCsv(String directory, String fileName, Set<Chromosome<Double>> population)
            throws IOException {
        try (CSVPrinter printer = new CSVPrinter(
                new FileWriter(new File(directory + "//" + fileName + ".csv")),
                CSVFormat.EXCEL
        )) {
            for (final Chromosome<?> chromosome : population) {
                printer.printRecord(chromosome.getFitness(), chromosome.getSize());
            }
        }
    }

    /**
     * @param args     The argument factory whose values will be used in constructing the evolutionary algorithm.
     * @param dm       The data manager.
     * @param supplier The chromosome supplier.
     * @return An evolutionary algorithm constructed according to the provided parameters.
     */
    private static EvolutionaryAlgorithm<CartesianChromosome<Double>, Double> getEvolutionaryAlgorithm(
            final ArgsConverterFactory args,
            final DataManager<Double> dm,
            final ChromosomeFactory<CartesianChromosome<Double>, Double> supplier
    ) {
        // Create the evolutionary algorithm
        EvolutionaryAlgorithm<CartesianChromosome<Double>, Double> ea =
                // TODO: generify with a factory so it isn't always SEAMO
                new SEAMO<>(supplier, 1);
        ea.setCrossoverProbability(args.getCrossoverProbability());
        ea.setMutationProbability(args.getMutationProbability());

        // Add in the evolutionary operators the algorithm can use
        ea.addMutator(new CartesianSingleActiveMutator<>());

        // Add in the objectives the algorithm should aim for
        ea.setObjective(
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
        return ea;
    }

    /**
     * Writes a given population to the specified graph writer.
     *
     * @param population The population to write
     * @param writer     The graph writer instance to write to
     */
    private static void drawGraph(final Set<Chromosome<Double>> population, final GraphWriter writer) {
        // Ignore malformed chromosomes
        population.stream().filter(chromosome ->
                chromosome.getFitness() != Double.POSITIVE_INFINITY &&
                        chromosome.getFitness() != Double.NEGATIVE_INFINITY
        ).forEach(writer::write);
    }

    /**
     * Pretty-prints a formatted progress indicator to the standard output stream.
     *
     * @param ea                The evolutionary algorithm being used.
     * @param generations       The maximum number of generations.
     * @param start             The start time.
     * @param currentGeneration The current generation.
     * @param trial             The current trial.
     */
    private static void printOutput(
            final EvolutionaryAlgorithm<CartesianChromosome<Double>, Double> ea,
            int generations,
            final Instant start,
            int currentGeneration,
            int trial
    ) {
        int percent = intToPercent(currentGeneration, generations);
        final StringBuilder out = new StringBuilder();

        // Ensure the algorithm used is SEAMO when including global bests
        String global = (ea instanceof SEAMO)
                ? " (" + ((SEAMO<?, Double>) ea).getGlobals().values().stream()
                .map(v -> String.format("%.4f", v)).collect(Collectors.joining(", ")) + ")"
                : "";

        // Print the output on a single line so it look like it's being updated
        out.append("\r")
                .append("Trial: ").append(trial)
                .append(" > Progress: ").append(percent).append("%")
                // And include the current best fitness
                .append("\t|\tGlobal Bests: ").append(global)
                .append("\t|\tGeneration: ").append(currentGeneration + 1)
                .append("\t|\tTime: ").append(Duration.between(start, Instant.now()))
                .append("           ");
        System.out.print(out);
    }

    /**
     * Constructs a new Client.
     */
    private Client() {
        this.args = new ArgsConverterFactory();
        this.argParser = new JCommander.Builder().programName("Iconic CLI").addObject(this.args).build();
    }

    /**
     * Parses the specified arguments.
     *
     * @param args An array of arguments
     */
    private void parse(final String[] args) {
        getArgParser().parse(args);
    }

    /**
     * Prints the usage instructions for the argument parser to the standard output stream.
     */
    private void usage() {
        getArgParser().usage();
    }

    /**
     * @return The argument parser of the client.
     */
    private JCommander getArgParser() {
        return argParser;
    }

    /**
     * @return The ArgsConverterFactory of the client.
     */
    private ArgsConverterFactory getArgs() {
        return args;
    }

    /**
     * Converts the provided progress into a percentage of the provided total.
     *
     * @param progress The current progress.
     * @param total    The total.
     * @return The current progress as a percentage of the total.
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