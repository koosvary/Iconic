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
import org.iconic.project.search.config.SearchConfigurationModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import static org.iconic.project.search.io.SearchState.*;

/**
 * <p>A model for evolutionary searches, it maintains a dataset, data manager, and a trainer
 *
 * <p>SearchExecutors implement the Runnable interface so that the search may be performed on a
 * separate thread.
 */
@Log4j2
public class SearchExecutor<T extends Chromosome<Double>> implements Runnable {

    private static final long PAUSE_SLEEP = 100;

    private final XYChart.Series<Number, Number> plots;
    private final DatasetModel datasetModel;
    private final ObservableList<String> _updates;
    private final ListProperty<String> updates;
    private final SearchConfigurationModel search;
    private final SolutionStorage<T> solutionStorage; // Stores the solutions found
    private final List<FunctionalPrimitive<Double, Double>> primitives;
    private EvolutionaryAlgorithm<T, Double> evolutionaryAlgorithm;

    private transient SimpleObjectProperty<SearchState> state;

    private transient Long lastUpdateTime;
    private transient Long startTime;
    private transient Long elapsedDuration;
    private transient Long timeSinceImprovement;
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
            @NonNull final SearchConfigurationModel search
            ) {
        this.datasetModel = datasetModel;
        this.search = search;
        this.generation = 0;
        this.plots = new XYChart.Series<>();
        this._updates = FXCollections.observableArrayList();
        this.updates = new SimpleListProperty<>(_updates);
        this.state = new SimpleObjectProperty<>(STOPPED);
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
        setState(RUNNING);
        setup();
        log.debug("Starting search");
        addUpdate("Starting...");

        Comparator<Chromosome<Double>> comparator = Comparator.comparing(Chromosome::getFitness);

        try {
            Chromosome<Double> bestCandidate = getEvolutionaryAlgorithm().getChromosomes()
                    .stream().min(comparator).get();
            addPlot(0, bestCandidate);
            addChromosomeUpdate(bestCandidate);

            for (generation = 1; (generation < search.getNumGenerations() || search.getNumGenerations() <= 0); generation++) {

                // Paused? We'll wait.
                while (getState() == PAUSED) {
                    Thread.sleep(PAUSE_SLEEP);
                }
                // Not running? Exit it.
                if (getState() == STOPPED) {
                    break;
                }

                startTime = System.currentTimeMillis();
                lastUpdateTime = startTime;
                updateSearchSettings();

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

                updateTimes();
                if (newCandidate) {
                    bestCandidate = newBestCandidate;
                    addChromosomeUpdate(bestCandidate);
                }
            }
        } catch (Exception ex) {
            log.error("{}: ", ex::getMessage);
            Arrays.stream(ex.getStackTrace()).forEach(log::error);
        } finally {
            updateTimes();
            log.debug("Stopping search");
            addUpdate("Finished!");
            setState(STOPPED);
        }
    }

    /**
     * Pauses the current search
     */
    public void pause() {
        setState(PAUSED);
    }

    /**
     * Stops any ongoing search.
     */
    public void stop() {
        setState(STOPPED);
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
        lastUpdateTime = startTime;
        elapsedDuration = 0L;
        timeSinceImprovement = 0L;
    }

    /**
     * Found a new best candidate
     * @param bestCandidate Chromosome
     */
    private void setImproved(Chromosome<?> bestCandidate) {
        addPlot(getGeneration(), bestCandidate);
        timeSinceImprovement = 0L;
        improvedCount++;
    }

    /**
     * Update time statistics
     */
    private void updateTimes() {
        if (startTime != null) {
            long current = System.currentTimeMillis();
            long diff = current - lastUpdateTime;

            lastUpdateTime = current;
            timeSinceImprovement += diff;
            elapsedDuration += current - startTime;
            startTime = null;
        }
    }

    /**
     * Add a plot point for progress over time.
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

    private void updateSearchSettings() {
        getEvolutionaryAlgorithm().setMutationProbability(search.getMutationRate());
        getEvolutionaryAlgorithm().setCrossoverProbability(search.getCrossoverRate());
    }

    /**
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

    public SimpleObjectProperty<SearchState> getStateProperty() {
        return state;
    }

    public SearchState getState() {
        return state.getValue();
    }

    @Synchronized
    public void setState(SearchState state) {
        this.state.set(state);
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

    public List<FunctionalPrimitive<Double, Double>> getPrimitives() {
        return primitives;
    }

    public SolutionStorage<T> getSolutionStorage() {
        return solutionStorage;
    }

    public Long getLastUpdateTime() {
        return lastUpdateTime;
    }

    public Long getElapsedDuration() {
        return elapsedDuration;
    }

    public Long getTimeSinceImprovement() {
        return timeSinceImprovement;
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