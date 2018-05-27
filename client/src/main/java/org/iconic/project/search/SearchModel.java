package org.iconic.project.search;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import lombok.NonNull;
import lombok.Synchronized;
import lombok.extern.log4j.Log4j2;
import org.iconic.ea.EvolutionaryAlgorithm;
import org.iconic.ea.chromosome.Chromosome;
import org.iconic.ea.chromosome.ExpressionChromosome;
import org.iconic.ea.data.DataManager;
import org.iconic.ea.gep.GeneExpressionProgramming;
import org.iconic.ea.operator.evolutionary.mutation.gep.ExpressionMutator;
import org.iconic.ea.operator.objective.DefaultObjective;
import org.iconic.ea.operator.objective.error.MeanSquaredError;
import org.iconic.ea.operator.primitive.Addition;
import org.iconic.ea.operator.primitive.Division;
import org.iconic.ea.operator.primitive.Multiplication;
import org.iconic.ea.operator.primitive.Subtraction;
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
        this.updates = new SimpleObjectProperty<>(null);
        this.running = false;
        this.ea = new GeneExpressionProgramming<>();

        this.updates.set("");

        DataManager<Double> dataManager = new DataManager<>(Double.class, datasetModel.getAbsolutePath());

        // Add in the functions it can use
        ea.addFunction(new Addition());
        ea.addFunction(new Subtraction());
        ea.addFunction(new Multiplication());
        ea.addFunction(new Division());

        // Add in the mutators it can use
        ea.addMutator(new ExpressionMutator<>());

        // Add in the objectives it should aim for
        ea.addObjective(
                new DefaultObjective<>(
                        new MeanSquaredError(), dataManager.getSamples())
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void run() {
        setRunning(true);

        final int populationSize = 100;
        final int numGenerations = 100;
        Comparator<Chromosome<Double>> comparator = Comparator.comparing(Chromosome::getFitness);

        while (isRunning()) {
            try {
                ea.initialisePopulation(populationSize);

                for (int i = 0; i < numGenerations; ++i) {
                    List<ExpressionChromosome<Double>> oldPopulation = ea.getChromosomes();
                    List<ExpressionChromosome<Double>> newPopulation = ea.evolve(oldPopulation);
                    ea.setChromosomes(newPopulation);

                    ExpressionChromosome<Double> bestCandidate = ea.getChromosomes()
                            .stream().min(comparator).get();

                    final String generation = "\nGeneration: " + i;
                    final String candidate = "\n\tBest candidate: " + bestCandidate.toString();
                    final String fitness = "\n\tFitness: " + bestCandidate.getFitness();

                    // Append the current generation's best results in front of the list of updates
                    setUpdates(
                            generation + candidate + fitness + getUpdates()
                    );

                    log.info(generation + candidate + fitness);
                }

                return;
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

    private void setRunning(boolean running) {
        this.running = running;
    }
}