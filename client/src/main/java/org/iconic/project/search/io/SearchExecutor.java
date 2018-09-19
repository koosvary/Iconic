package org.iconic.project.search.io;

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
import org.iconic.ea.operator.evolutionary.crossover.gep.SimpleExpressionCrossover;
import org.iconic.ea.operator.evolutionary.mutation.gep.ExpressionMutator;
import org.iconic.ea.operator.objective.DefaultObjective;
import org.iconic.ea.operator.objective.error.MeanSquaredError;
import org.iconic.ea.operator.primitive.*;
import org.iconic.ea.strategies.gep.GeneExpressionProgramming;
import org.iconic.project.BlockDisplay;
import org.iconic.project.dataset.DatasetModel;

import java.util.ArrayList;
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
    private EvolutionaryAlgorithm<T, Double> evolutionaryAlgorithm;
    private boolean running;

    /**
     * Constructs a new search model with the provided dataset.
     *
     * @param datasetModel The dataset to perform the search on
     */
    public SearchExecutor(@NonNull final DatasetModel datasetModel, int numGenerations) {
        this.datasetModel = datasetModel;
        this.numGenerations = getNumGenerations();
//        ExpressionChromosomeFactory<Double> supplier = new ExpressionChromosomeFactory<>(
//                10,
//                datasetModel.getDataManager().getFeatureSize() - 1
//        );
////
//        List<FunctionalPrimitive<Double, Double>> enabledPrimitives = new ArrayList<>(this.blockDisplays.size());
//        for (int i = 0; i < this.blockDisplays.size(); i++) {
//            if (this.blockDisplays.get(i).isEnabled()) {
//                enabledPrimitives.add(getPrimitives().get(i));
//            }
//        }

//        supplier.addFunction(enabledPrimitives);

        this.plots = new XYChart.Series<>();
        this.updates = new SimpleObjectProperty<>(null);
        this.running = false;
        this.updates.set("");
        this.plots.setName(this.datasetModel.getName());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void run() {
        setRunning(true);
        Comparator<Chromosome<Double>> comparator = Comparator.comparing(Chromosome::getFitness);

        while (isRunning()) {
            try {
                Chromosome<Double> bestCandidate = getEvolutionaryAlgorithm().getChromosomes()
                        .stream().min(comparator).get();

                setUpdates("\nStarting..." + getUpdates());
                for (int i = 0; i < getNumGenerations() && isRunning(); ++i) {
                    List<T> oldPopulation = getEvolutionaryAlgorithm().getChromosomes();
                    List<T> newPopulation = getEvolutionaryAlgorithm().evolve(oldPopulation);
                    getEvolutionaryAlgorithm().setChromosomes(newPopulation);

                    T newBestCandidate = getEvolutionaryAlgorithm().getChromosomes()
                            .stream().min(comparator).get();

                    // Only add a new plot point if the fitness value improves
                    boolean newCandidate = bestCandidate.getFitness() > newBestCandidate.getFitness();

                    if (newCandidate) {
                        getPlots().getData().add(new XYChart.Data<>(i + 1, newBestCandidate.getFitness()));
                    }

                    bestCandidate = newBestCandidate;

                    final String generation = "\nGeneration: " + (i + 1);
                    final String candidate = "\n\tNew Best candidate: " + bestCandidate.toString();
                    final String fitness = "\n\tFitness: " + bestCandidate.getFitness();

                    // Append the current generation's best results in front of the list of updates
                    if (newCandidate) {
                        setUpdates(
                                generation + candidate + fitness + getUpdates()
                        );
                    }

                    log.debug(generation + candidate + fitness);
                }

                setUpdates("\nFinished!" + getUpdates());
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
        return numGenerations;
    }
}