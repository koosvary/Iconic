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
package org.iconic.project.search.io;

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.chart.XYChart;
import lombok.NonNull;
import lombok.Synchronized;
import lombok.extern.log4j.Log4j2;
import org.iconic.ea.EvolutionaryAlgorithm;
import org.iconic.ea.chromosome.Chromosome;
import org.iconic.ea.operator.primitive.FunctionalPrimitive;
import org.iconic.project.dataset.DatasetModel;
import org.iconic.project.search.SolutionStorage;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * <p>A model for evolutionary searches, it maintains a dataset, data manager, and a trainer</p>
 *
 * <p>SearchExecutors implement the Runnable interface so that the search may be performed on a
 * separate thread.</p>
 */
@Log4j2
public class SearchExecutor<T extends Chromosome<Double>> implements Runnable {
    private final XYChart.Series<Number, Number> plots;
    private final DatasetModel datasetModel;
    private final ObjectProperty<String> updates;
    private final int numGenerations;
    private final SolutionStorage<T> solutionStorage; // Stores the solutions found
    private final List<FunctionalPrimitive<Double, Double>> primitives;
    private EvolutionaryAlgorithm<T, Double> evolutionaryAlgorithm;
    private boolean running;

    private transient Long startTime;
    private transient Long elapsedDuration;
    private transient Long lastImproveTime;
    private transient int improvedCount;
    private transient int generation;

    /**
     * Constructs a new search model with the provided dataset.
     *
     * @param datasetModel The dataset to perform the search on
     */
    public SearchExecutor(
            @NonNull final DatasetModel datasetModel,
            @NonNull final List<FunctionalPrimitive<Double, Double>> primitives,
            int numGenerations
    ) {
        this.datasetModel = datasetModel;
        this.numGenerations = numGenerations;
        this.plots = new XYChart.Series<>();
        this.updates = new SimpleObjectProperty<>(null);
        this.running = false;
        this.updates.set("");
        this.plots.setName(this.datasetModel.getName());
        this.primitives = primitives;
        this.solutionStorage = new SolutionStorage<>();
        this.startTime = null;
        this.elapsedDuration = null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void run() {
        setRunning(true);
        setup();
        log.debug("Starting search");
        Comparator<Chromosome<Double>> comparator = Comparator.comparing(Chromosome::getFitness);

        while (isRunning()) {
            try {
                Chromosome<Double> bestCandidate = getEvolutionaryAlgorithm().getChromosomes()
                        .stream().min(comparator).get();
                addPlot(0, bestCandidate);
                setUpdates("\nStarting..." + getUpdates());

                for (generation = 1; (generation <= getNumGenerations() || getNumGenerations() == 0) && isRunning(); ++generation) {
                    List<T> oldPopulation = getEvolutionaryAlgorithm().getChromosomes();
                    List<T> newPopulation = getEvolutionaryAlgorithm().evolve(oldPopulation);
                    getEvolutionaryAlgorithm().setChromosomes(newPopulation);

                    // Evaluate the new population of solutions and store the best ones
                    solutionStorage.evaluate(newPopulation);

                    T newBestCandidate = getEvolutionaryAlgorithm().getChromosomes().stream().min(comparator).get();

                    // Only add a new plot point if the fitness value improves
                    boolean newCandidate = bestCandidate.getFitness() > newBestCandidate.getFitness();

                    if (newCandidate) {
                        bestCandidate = newBestCandidate;
                        setImproved(bestCandidate);

                        final String gen = "\nGeneration: " + getGeneration();
                        final String candidate = "\n\tNew Best candidate: " + bestCandidate.toString();
                        final String fitness = "\n\tFitness: " + bestCandidate.getFitness();
                        final String size = "\n\tSize: " + bestCandidate.getSize();

                        // Append the current generation's best results in front of the list of updates
                        setUpdates(
                                gen + candidate + fitness + size + getUpdates()
                        );
                        log.debug(generation + candidate + fitness + size);
                    }
                    elapsedDuration = System.currentTimeMillis() - startTime;
                }
            } catch (Exception ex) {
                log.error("{}: ", ex::getMessage);
                Arrays.stream(ex.getStackTrace()).forEach(log::error);
            } finally {
                setUpdates("\nFinished!" + getUpdates());
                setRunning(false);
                elapsedDuration = System.currentTimeMillis() - startTime;
                log.debug("Stopping search");
            }
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

    private void setImproved(Chromosome<?> bestCandidate) {
        addPlot(getGeneration(), bestCandidate);
        lastImproveTime = System.currentTimeMillis();
        improvedCount++;
    }

    /**
     * Add a plot point for progress over time.
     *
     * @param time      Time in generations.
     * @param candidate Candidate to plot.
     */
    private void addPlot(int time, final Chromosome<?> candidate) {
        Platform.runLater(() -> {
                    double fitness = candidate.getFitness();
                    XYChart.Data<Number, Number> plot = new XYChart.Data<>(time, fitness);
                    getPlots().getData().add(plot);
                }
        );
    }

    /**
     * Stops any ongoing search.
     */
    public void stop() {
        setRunning(false);
    }

    /**
     * Returns the dataset that's being trained on.
     *
     * @return The dataset that this search executor is training on.
     */
    public DatasetModel getDatasetModel() {
        return datasetModel;
    }

    public String getUpdates() {
        return updates.get();
    }

    public ObjectProperty<String> updatesProperty() {
        return updates;
    }

    @Synchronized
    private void setUpdates(String updates) {
        Platform.runLater(() ->
                this.updates.set(updates)
        );
    }

    public boolean isRunning() {
        return running;
    }

    @Synchronized
    public void setRunning(boolean running) {
        this.running = running;
    }

    public XYChart.Series<Number, Number> getPlots() {
        return plots;
    }

    public EvolutionaryAlgorithm<T, Double> getEvolutionaryAlgorithm() {
        return evolutionaryAlgorithm;
    }

    public void setEvolutionaryAlgorithm(EvolutionaryAlgorithm<T, Double> evolutionaryAlgorithm) {
        this.evolutionaryAlgorithm = evolutionaryAlgorithm;
    }

    public int getNumGenerations() {
        return (numGenerations > 0) ? numGenerations : 0;
    }

    public List<FunctionalPrimitive<Double, Double>> getPrimitives() {
        return primitives;
    }

    public SolutionStorage<T> getSolutionStorage() {
        return solutionStorage;
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