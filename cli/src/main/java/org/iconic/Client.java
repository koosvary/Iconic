/**
 * Copyright 2018 Iconic
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
import org.iconic.utils.SeriesWriter;
import org.iconic.utils.XYGraphWriter;
import org.iconic.utils.XYSeriesWriter;
import org.knowm.xchart.*;
import org.knowm.xchart.style.markers.SeriesMarkers;

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
    private final List<List<Chromosome<?>>> nonDominatedSolutions;

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
                    new Exponential(), new Root(), new Power(), new Sin()
            ));

            final int generations = client.getArgs().getGenerations();
            final Set<Chromosome<Double>> nonDominatedFinal = new LinkedHashSet<>();
            final List<Set<Chromosome<Double>>> nonDominatedAll = new ArrayList<>(generations);

            for (int i = 0; i < generations; ++i) {
                nonDominatedAll.add(new LinkedHashSet<>());
            }

            EvolutionaryAlgorithm<CartesianChromosome<Double>, Double> ea = getEvolutionaryAlgorithm(
                    client.getArgs(), dm, supplier
            );

            // Start the evolutionary loop
            final Instant start = Instant.now();
            for (int trial = 0; trial < client.getArgs().getRepetitions(); ++trial) {
                // Initialise the population
                ea.initialisePopulation(client.getArgs().getPopulation());
                List<CartesianChromosome<Double>> population = ea.getChromosomes();

                for (int i = 0; i < generations; ++i) {
                    population = ea.evolve(population);
                    // Pretty-print a summarised progress indicator
                    printOutput(ea, generations, start, i, trial + 1);

                    // Store the current global best values
                    nonDominatedAll.get(i).addAll(
                            ((SEAMO<CartesianChromosome<Double>, Double>) ea)
                                    .getGlobalChromosomes().values()
                    );
                }

                nonDominatedFinal.addAll(
                        ((SEAMO<CartesianChromosome<Double>, Double>) ea)
                                .getGlobalChromosomes().values()
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
                    exportCsv(directory, "results-all-gen", nonDominatedAll.stream().reduce(
                            new LinkedHashSet<>(), (x, y) -> {
                                x.addAll(y);
                                return x;
                            })
                    );
                }
                // Print and export a graph of the solutions plotted by their dimensions
                if (client.getArgs().isGraph()) {
                    GraphWriter<XYSeries> graphWriter = new XYGraphWriter("Sum of Mean Squared Error", "Size");

                    for (int i = 0; i < nonDominatedAll.size(); ++i) {
                        SeriesWriter<XYSeries> series = new XYSeriesWriter(
                                "Plot of Generation " + (i + 1),
                                XYSeries.XYSeriesRenderStyle.Scatter, SeriesMarkers.CROSS,
                                Chromosome::getFitness, Chromosome::getSize
                        );
                        nonDominatedAll.get(i).forEach(series::write);
                        graphWriter.write(series.draw());
                    }

                    graphWriter.export("All Generations - Non-Dominated", directory, "results-all");
                    graphWriter.clear();

                    SeriesWriter<XYSeries> seriesWriter = new XYSeriesWriter(
                            "Plot of Non-Dominated Solutions",
                            XYSeries.XYSeriesRenderStyle.Scatter, SeriesMarkers.CROSS,
                            Chromosome::getFitness, Chromosome::getSize
                    );

                    nonDominatedFinal.forEach(seriesWriter::write);
                    graphWriter.write(seriesWriter.draw());
                    graphWriter.export("Last Generation - Non-Dominated", directory, "results-last");

                    final Map<Objective<Double>, Chromosome<Double>> globals = new HashMap<>();
                    final MultiObjective<Double> multiObjective = (MultiObjective<Double>) ea.getObjective();

                    multiObjective.getGoals().forEach(goal ->
                            nonDominatedFinal.forEach(chromosome -> {
                                if (!globals.containsKey(goal)) {
                                    globals.put(goal, chromosome);
                                } else if (goal.isNotWorse(
                                        goal.apply(chromosome),
                                        goal.apply(globals.get(goal))
                                )) {
                                    globals.put(goal, chromosome);
                                }
                            })
                    );

                    graphSolutionFitPlot(
                            new ArrayList<>(supplier.getFunctionalPrimitives()),
                            new HashSet<>(globals.values()), dm, directory, "solution-fit"
                    );
                }
            } catch (IOException ex) {
                log.error("{}: {}", ex::getMessage, ex::getCause);
            }
        }
    }

    private static void graphSolutionFitPlot(
            final List<FunctionalPrimitive<?, ?>> primitives,
            final Set<Chromosome<Double>> globals,
            final DataManager<Double> dm,
            final String directory,
            final String fileName
    ) throws IOException {
        final GraphWriter<XYSeries> graphWriter = new XYGraphWriter("Sample", "Value");
        final SeriesWriter<XYSeries> expectedSeries = new XYSeriesWriter(
                "Plot of Actual Values", XYSeries.XYSeriesRenderStyle.Line, SeriesMarkers.NONE
        );

        final List<Number> expectedValues = dm.getSampleColumn(dm.getSampleHeaders().get(
                dm.getFeatureSize() - 1
        ));

        for (int i = 0; i < expectedValues.size(); ++i) {
            expectedSeries.write(i + 1, expectedValues.get(i).doubleValue());
        }

        graphWriter.write(expectedSeries.draw());

        globals.forEach(chromosome -> {
            final SeriesWriter<XYSeries> actualSeries = new XYSeriesWriter(
                    String.format("Plot of (%.4f, %d)", chromosome.getFitness(), chromosome.getSize()),
                    XYSeries.XYSeriesRenderStyle.Line, SeriesMarkers.NONE
            );

            final List<Double> actualValues = chromosome.evaluate(dm).stream().map(outputs ->
                    outputs.values().stream().mapToDouble(Double::doubleValue).sum()
            ).collect(Collectors.toList());

            for (int i = 0; i < actualValues.size(); ++i) {
                actualSeries.write(i + 1, actualValues.get(i));
            }

            graphWriter.write(actualSeries.draw());
        });

        expectedSeries.clear();
        graphWriter.export("Solution Fit Plot", directory, fileName);
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
        this.nonDominatedSolutions = new ArrayList<>();
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

    public List<List<Chromosome<?>>> getNonDominatedSolutions() {
        return nonDominatedSolutions;
    }
}