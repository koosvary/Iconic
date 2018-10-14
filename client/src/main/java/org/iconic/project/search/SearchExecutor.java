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
package org.iconic.project.search;

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.chart.XYChart;
import lombok.NonNull;
import lombok.Synchronized;
import lombok.extern.log4j.Log4j2;
import org.iconic.ea.EvolutionaryAlgorithm;
import org.iconic.ea.chromosome.Chromosome;
import org.iconic.ea.chromosome.expression.ExpressionChromosome;
import org.iconic.ea.operator.objective.CacheableObjective;
import org.iconic.ea.strategies.gep.GeneExpressionProgramming;
import org.iconic.ea.chromosome.expression.ExpressionChromosomeFactory;
import org.iconic.ea.data.FeatureClass;
import org.iconic.ea.operator.evolutionary.crossover.gep.SimpleExpressionCrossover;
import org.iconic.ea.operator.evolutionary.mutation.gep.ExpressionMutator;
import org.iconic.ea.operator.objective.DefaultObjective;
import org.iconic.ea.operator.objective.error.MeanSquaredError;
import org.iconic.ea.operator.primitive.*;
import org.iconic.project.BlockDisplay;
import org.iconic.project.dataset.DatasetModel;

import java.util.*;

/**
 * <p>A global for evolutionary searches, it maintains a dataset, data manager, and a trainer.</p>
 * <p>SearchModels implement the Runnable interface so that the search may be performed on a separate thread.</p>
 */
@Log4j2
public class SearchExecutor implements Runnable {
    private static final FunctionalPrimitive[] FUNCTIONAL_PRIMITIVES = {new AbsoluteValue(), new Addition(), new And(), new ArcCos(), new ArcSin(),
            new ArcTan(), new Ceiling(), new Cos(), new Division(), new EqualTo(),
            new Exponential(), new Floor(), new GaussianFunction(), new GreaterThan(),
            new GreaterThanOrEqual(), new IfThenElse(), new LessThan(), new LessThanOrEqual(),
            new LogisticFunction(), new Maximum(), new Minimum(), new Modulo(), new Multiplication(),
            new NaturalLog(), new Negation(), new Not(), new Or(), new Power(), new Root(),
            new SignFunction(), new Sin(), new SquareRoot(), new StepFunction(), new Subtraction(),
            new Tan(), new Tanh(), new TwoArcTan(), new Xor()};

    private final XYChart.Series<Number, Number> plots;
    private final DatasetModel datasetModel;
    private final ObjectProperty<String> updates;
    private EvolutionaryAlgorithm<ExpressionChromosome<Double>, Double> ea;
    private SolutionStorage<Double> solutionStorage = new SolutionStorage<>(); // Stores the solutions found
    private boolean running;
    private ArrayList<BlockDisplay> blockDisplays;

    private transient Long startTime;
    private transient Long elapsedDuration;
    private transient Long lastImproveTime;
    private transient int improvedCount;
    private transient int generation;

    /**
     * Constructs a new search model with the provided dataset.
     * @param datasetModel The dataset to perform the search on
     */
    public SearchExecutor(@NonNull final DatasetModel datasetModel, ArrayList<BlockDisplay> blockDisplays) {

        // Get the number of features to tell the search function how many it can use
        int numFeatures = 0;
        List<String> headers = datasetModel.getDataManager().getSampleHeaders();

        for (String header : headers) {
            FeatureClass<Number> feature = datasetModel.getDataManager().getDataset().get(header);

            if (feature.isActive()) {
                numFeatures++;
            }
        }

        ExpressionChromosomeFactory<Double> supplier = new ExpressionChromosomeFactory<>(
                10,
                numFeatures
        );

        this.blockDisplays = new ArrayList<>(blockDisplays);
        this.datasetModel = datasetModel;

        List<FunctionalPrimitive<Double, Double>> enabledPrimitives = new ArrayList<>(this.blockDisplays.size());
        for (int i = 0; i < this.blockDisplays.size(); i++) {
            if (this.blockDisplays.get(i).isEnabled()) {
                enabledPrimitives.add(getFunctionalPrimitives()[i]);
            }
        }

        supplier.addFunction(enabledPrimitives);

        this.plots = new XYChart.Series<>();
        this.updates = new SimpleObjectProperty<>(null);
        this.running = false;
        this.ea = new GeneExpressionProgramming<>(supplier);
        this.updates.set("");
        this.plots.setName(this.datasetModel.getName());

        this.startTime = null;
        this.elapsedDuration = null;

        ea.setCrossoverProbability(1.0);
        ea.setMutationProbability(1.0);

        // Add in the evolutionary operators the algorithm can use
        ea.addCrossover(new SimpleExpressionCrossover<>());
        ea.addMutator(new ExpressionMutator<>());

        // Add in the objectives the algorithm should aim for
        ea.setObjective(
                new CacheableObjective<>(
                        new DefaultObjective(
                                new MeanSquaredError(), datasetModel.getDataManager()
                        ))
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void run() {
        log.debug("Starting search...");
        setRunning(true);
        setup();

        final int populationSize = 5;
        Comparator<Chromosome<Double>> comparator = Comparator.comparing(Chromosome::getFitness);

        try {
            ea.initialisePopulation(populationSize);

            ExpressionChromosome<Double> bestCandidate = ea.getChromosomes().stream().min(comparator).get();
            addPlot(bestCandidate);

            setUpdates("\nStarting..." + getUpdates());
            for (generation = 1; isRunning(); generation++) {
                List<ExpressionChromosome<Double>> oldPopulation = ea.getChromosomes();
                List<ExpressionChromosome<Double>> newPopulation = ea.evolve(oldPopulation);
                ea.setChromosomes(newPopulation);

                // Evaluate the new population of solutions and store the best ones
                solutionStorage.evaluate(newPopulation); // TODO - Choose what solutions get stored

                ExpressionChromosome<Double> newBestCandidate = ea.getChromosomes().stream().min(comparator).get();

                // Only add a new plot point if the fitness value improves
                boolean newCandidate = bestCandidate.getFitness() > newBestCandidate.getFitness();

                if (newCandidate) {
                    bestCandidate = newBestCandidate;
                    setImproved(bestCandidate);
                }

                final String gen = "\nGeneration: " + generation;
                final String candidate = "\n\tNew Best candidate: " + bestCandidate.toString();
                final String fitness = "\n\tFitness: " + bestCandidate.getFitness();

                // Append the current generation's best results in front of the list of updates
                if (newCandidate) {
                    setUpdates(gen + candidate + fitness + getUpdates());
                }

                elapsedDuration = System.currentTimeMillis() - startTime;
                log.info(gen + candidate + fitness);
            }

        } catch (Exception ex) {
            log.error("{}: ", ex::getMessage);
            Arrays.stream(ex.getStackTrace()).forEach(log::error);
        } finally {
            setUpdates("\nFinished!" + getUpdates());
            elapsedDuration = System.currentTimeMillis() - startTime;
            setRunning(false);
        }
    }

    /**
     * Set the transient variables for an execution
     */
    private void setup() {
        startTime = System.currentTimeMillis();
        elapsedDuration = 0L;
        lastImproveTime = startTime;
        improvedCount = 0;
        generation = 0;
    }

    private void setImproved(ExpressionChromosome<Double> bestCandidate) {
        addPlot(bestCandidate);
        lastImproveTime = System.currentTimeMillis();
        improvedCount++;
    }

    /**
     * Stops any ongoing search.
     */
    public void stop() {
        setRunning(false);
    }

    /**
     * Add a plot point for progress over time
     * @param candidate Candidate to plot
     */
    private void addPlot(final ExpressionChromosome<Double> candidate) {
        Platform.runLater(() -> {
            double fitness = candidate.getFitness();
            XYChart.Data<Number, Number> plot = new XYChart.Data<>(generation, fitness);
            getPlots().getData().add(plot);
        });
    }

    /**
     * Returns the dataset that's being trained on.
     * @return The dataset that this search model is training on
     */
    public DatasetModel getDatasetModel() {
        return datasetModel;
    }

    public SolutionStorage<Double> getSolutionStorage() {
        return solutionStorage;
    }

    @Synchronized
    public String getUpdates() {
        return updates.get();
    }

    @Synchronized
    public ObjectProperty<String> updatesProperty() {
        return updates;
    }

    @Synchronized
    private void setUpdates(String updates) {
        this.updates.set(updates);
    }

    public boolean isRunning() {
        return running;
    }

    @Synchronized
    private void setRunning(boolean running) {
        this.running = running;
    }

    public XYChart.Series<Number, Number> getPlots() {
        return plots;
    }

    public static FunctionalPrimitive[] getFunctionalPrimitives() {
        return FUNCTIONAL_PRIMITIVES;
    }

    public Long getStartTime() {
        return startTime;
    }

    public Long getElapsedDuration() {
        return elapsedDuration;
    }

    public Long getLastImproveTime() {
        return lastImproveTime;
    }

    public Long getAverageImproveDuration() {
        if (improvedCount != 0L) {
            return getElapsedDuration() / improvedCount;
        } else {
            return 0L;
        }
    }

    public int getGeneration() {
        return generation;
    }
}