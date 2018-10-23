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
package org.iconic.project.search.io;

import javafx.application.Platform;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart;
import lombok.NonNull;
import lombok.Synchronized;
import lombok.extern.log4j.Log4j2;
import org.iconic.ea.strategies.EvolutionaryAlgorithm;
import org.iconic.ea.chromosome.Chromosome;
import org.iconic.ea.operator.objective.Objective;
import org.iconic.ea.operator.primitive.FunctionalPrimitive;
import org.iconic.project.dataset.DatasetModel;
import org.iconic.project.search.SolutionStorage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * <p>A model for evolutionary searches, it maintains a dataset, data manager, and a trainer
 *
 * <p>SearchExecutors implement the Runnable interface so that the search may be performed on a
 * separate thread.
 */
@Log4j2
public class SearchExecutor<T extends Chromosome<Double>> implements Runnable {
    private final XYChart.Series<Number, Number> plots;
    private final DatasetModel datasetModel;
    private final ObservableList<String> _updates;
    private final ListProperty<String> updates;
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
        this.generation = 0;
        this.plots = new XYChart.Series<>();
        this._updates = FXCollections.observableArrayList();
        this.updates = new SimpleListProperty<>(_updates);
        this.running = false;
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
                addUpdate("Starting...");
                addChromosomeUpdate(bestCandidate);

                for (generation = 1; (generation <= getNumGenerations() || getNumGenerations() == 0) && isRunning(); ++generation) {
                    List<T> oldPopulation = getEvolutionaryAlgorithm().getChromosomes();
                    List<T> newPopulation = getEvolutionaryAlgorithm().evolve(oldPopulation);
                    getEvolutionaryAlgorithm().setChromosomes(newPopulation);

                    // Evaluate the new population of solutions and store the best ones
                    solutionStorage.evaluate(newPopulation);

                    T newBestCandidate = getEvolutionaryAlgorithm().getChromosomes().stream().min(comparator).get();
                    Objective<?> objective = getEvolutionaryAlgorithm().getObjective();

                    // Only add a new plot point if the fitness value improves
                    boolean newCandidate = objective.isNotWorse(
                            newBestCandidate.getFitness(),
                            bestCandidate.getFitness()
                    ) && !objective.isEqual(
                            newBestCandidate.getFitness(),
                            bestCandidate.getFitness()
                    );

                    if (newCandidate) {
                        bestCandidate = newBestCandidate;
                        addChromosomeUpdate(bestCandidate);
                    }
                    elapsedDuration = System.currentTimeMillis() - startTime;
                }
            } catch (Exception ex) {
                log.error("{}: ", ex::getMessage);
                Arrays.stream(ex.getStackTrace()).forEach(log::error);
            } finally {
                addUpdate("Finished!");
                setRunning(false);
                elapsedDuration = System.currentTimeMillis() - startTime;
                log.debug("Stopping search");
            }
        }
    }

    private void addChromosomeUpdate(Chromosome<?> chromosome) {
        setImproved(chromosome);
        final String gen = "Generation: " + generation;
        final String candidate = chromosome.simplifyExpression(
                chromosome.getExpression(chromosome.toString(), new ArrayList<>(primitives), true)
        );
        final String fitness = "Fitness: " + chromosome.getFitness();
        final String size = "Size: " + chromosome.getSize();
        addUpdate(gen);
        addUpdate(candidate);
        addUpdate(fitness);
        addUpdate(size);
        log.debug(gen + candidate + fitness + size);
    }

    private void addUpdate(final String value) {
        Platform.runLater(() -> get_updates().add(value));
    }

    /**
     * Set the transient variables for an execution
     */
    private void setup() {
        startTime = System.currentTimeMillis();
        elapsedDuration = 0L;
        lastImproveTime = startTime;
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

    private ObservableList<String> get_updates() {
        return _updates;
    }

    public ObservableList<String> getUpdates() {
        return updates.get();
    }

    public ListProperty<String> updatesProperty() {
        return updates;
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