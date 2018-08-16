package org.iconic.project.search;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.chart.XYChart;
import lombok.NonNull;
import lombok.Synchronized;
import lombok.extern.log4j.Log4j2;
import org.iconic.ea.EvolutionaryAlgorithm;
import org.iconic.ea.chromosome.Chromosome;
import org.iconic.ea.chromosome.expression.ExpressionChromosome;
import org.iconic.ea.chromosome.expression.ExpressionChromosomeFactory;
import org.iconic.ea.gep.GeneExpressionProgramming;
import org.iconic.ea.operator.evolutionary.crossover.gep.SimpleExpressionCrossover;
import org.iconic.ea.operator.evolutionary.mutation.gep.ExpressionMutator;
import org.iconic.ea.operator.objective.DefaultObjective;
import org.iconic.ea.operator.objective.error.MeanSquaredError;
import org.iconic.ea.operator.primitive.*;
import org.iconic.project.dataset.DatasetModel;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * <p>
 * A global for evolutionary searches, it maintains a dataset, data manager, and a trainer.
 * </p>
 * <p>
 * SearchModels implement the Runnable interface so that the search may be performed on a separate thread.
 * </p>
 */
@Log4j2
public class SearchModel implements Runnable {
    private final XYChart.Series<Number, Number> plots;
    private final DatasetModel datasetModel;
    private final ObjectProperty<String> updates;
    private EvolutionaryAlgorithm<ExpressionChromosome<Double>, Double> ea;
    private boolean running;

    /**
     * Constructs a new search model with tne provided dataset.
     *
     * @param datasetModel The dataset to perform the search on
     */
    public SearchModel(@NonNull final DatasetModel datasetModel) {
        this.datasetModel = datasetModel;

        ExpressionChromosomeFactory<Double> supplier = new ExpressionChromosomeFactory<>(
                10,
                datasetModel.getDataManager().getFeatureSize()
        );

        // Add in the functions the chromosomes can use
        supplier.addFunction(Arrays.asList(
                new Addition(), new Subtraction(), new Multiplication(), new Division(),
                new Power(), new Root(), new Sin(), new Cos(), new Tan()
        ));

        this.plots = new XYChart.Series<>();
        this.updates = new SimpleObjectProperty<>(null);
        this.running = false;
        this.ea = new GeneExpressionProgramming<>(supplier);
        this.updates.set("");
        this.plots.setName(this.datasetModel.getName());

        ea.setCrossoverProbability(1.0);
        ea.setMutationProbability(1.0);

        // Add in the evolutionary operators the algorithm can use
        ea.addCrossover(new SimpleExpressionCrossover<>());
        ea.addMutator(new ExpressionMutator<>());

        // Add in the objectives the algorithm should aim for
        ea.addObjective(
                new DefaultObjective<>(
                        new MeanSquaredError(), datasetModel.getDataManager())
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void run() {
        setRunning(true);

        final int populationSize = 100;
        final int numGenerations = 500;
        Comparator<Chromosome<Double>> comparator = Comparator.comparing(Chromosome::getFitness);

        while (isRunning()) {
            try {
                ea.initialisePopulation(populationSize, getDatasetModel().getDataManager().getFeatureSize());

                ExpressionChromosome<Double> bestCandidate = ea.getChromosomes()
                        .stream().min(comparator).get();

                for (int i = 0; i < numGenerations && isRunning(); ++i) {
                    List<ExpressionChromosome<Double>> oldPopulation = ea.getChromosomes();
                    List<ExpressionChromosome<Double>> newPopulation = ea.evolve(oldPopulation);
                    ea.setChromosomes(newPopulation);

                    ExpressionChromosome<Double> newBestCandidate = ea.getChromosomes()
                            .stream().min(comparator).get();

                    // Only add a new plot point if the fitness value improves
                    if (bestCandidate.getFitness() > newBestCandidate.getFitness()) {
                        getPlots().getData().add(new XYChart.Data<>(i + 1, newBestCandidate.getFitness()));
                    }

                    bestCandidate = newBestCandidate;

                    final String generation = "\nGeneration: " + (i + 1);
                    final String candidate = "\n\tBest candidate: " + bestCandidate.toString();
                    final String fitness = "\n\tFitness: " + bestCandidate.getFitness();

                    // Append the current generation's best results in front of the list of updates
                    setUpdates(
                            generation + candidate + fitness + getUpdates()
                    );

                    log.info(generation + candidate + fitness);
                }

                setRunning(false);
            } catch (Exception ex) {
                log.error("{}: ", ex::getMessage);
                Arrays.stream(ex.getStackTrace()).forEach(log::error);
            }
        }

    }

    /**
     * <p>
     * Stops any ongoing search.
     * </p>
     */
    public void stop() {
        setRunning(false);
    }

    /**
     * <p>
     * Returns the dataset that's being trained on.
     * </p>
     *
     * @return The dataset that this search model is training on
     */
    public DatasetModel getDatasetModel() {
        return datasetModel;
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
}