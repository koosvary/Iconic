package org.iconic.project.search.config;

import javafx.beans.property.SimpleIntegerProperty;
import lombok.NonNull;
import org.iconic.ea.EvolutionaryAlgorithm;
import org.iconic.ea.chromosome.expression.ExpressionChromosome;
import org.iconic.ea.chromosome.expression.ExpressionChromosomeFactory;
import org.iconic.ea.operator.evolutionary.crossover.gep.SimpleExpressionCrossover;
import org.iconic.ea.operator.evolutionary.mutation.gep.ExpressionMutator;
import org.iconic.ea.operator.objective.DefaultObjective;
import org.iconic.ea.operator.objective.error.MeanSquaredError;
import org.iconic.ea.strategies.gep.GeneExpressionProgramming;
import org.iconic.project.search.io.SearchExecutor;

public class GepConfigurationModel extends SearchConfigurationModel {
    private SimpleIntegerProperty headLength;

    /**
     * {@inheritDoc}
     */
    public GepConfigurationModel(@NonNull final String name) {
        super(name);
        this.headLength = new SimpleIntegerProperty(1);
    }

    @Override
    protected SearchExecutor<?> buildSearchExecutor() {
        if (!isValid()) {
            return null;
        }

        ExpressionChromosomeFactory<Double> supplier =
                new ExpressionChromosomeFactory<>(
                        getHeadLength(),
                        getDatasetModel().getDataManager().getFeatureSize() - 1
                );
        supplier.addFunction(getPrimitives());

        EvolutionaryAlgorithm<ExpressionChromosome<Double>, Double> ea =
                new GeneExpressionProgramming<>(supplier);
        ea.addMutator(new ExpressionMutator<>());
        ea.addCrossover(new SimpleExpressionCrossover<>());
        ea.setCrossoverProbability(getCrossoverRate());
        ea.setMutationProbability(getMutationRate());
        ea.addObjective(
                new DefaultObjective<>(
                        new MeanSquaredError(), getDatasetModel().getDataManager()
                )
        );
        ea.initialisePopulation(getPopulationSize());

        SearchExecutor<ExpressionChromosome<Double>> searchExecutor =
                new SearchExecutor<>(getDatasetModel(), getNumGenerations());
        searchExecutor.setEvolutionaryAlgorithm(ea);

        return searchExecutor;
    }

    @Override
    protected boolean isValid() {
        return getDatasetModel() != null;
    }

    public int getHeadLength() {
        return headLength.get();
    }

    public SimpleIntegerProperty headLengthProperty() {
        return headLength;
    }

    public void setHeadLength(int headLength) {
        setChanged(true);
        this.headLength.set(headLength);
    }
}
