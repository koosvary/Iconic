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
package org.iconic.project.search.config;

import javafx.beans.property.SimpleIntegerProperty;
import lombok.NonNull;
import org.iconic.ea.EvolutionaryAlgorithm;
import org.iconic.ea.chromosome.expression.ExpressionChromosome;
import org.iconic.ea.chromosome.expression.ExpressionChromosomeFactory;
import org.iconic.ea.operator.evolutionary.crossover.gep.SimpleExpressionCrossover;
import org.iconic.ea.operator.evolutionary.mutation.gep.ExpressionMutator;
import org.iconic.ea.operator.objective.CacheableObjective;
import org.iconic.ea.operator.objective.DefaultObjective;
import org.iconic.ea.operator.objective.error.MeanSquaredError;
import org.iconic.ea.strategies.gep.GeneExpressionProgramming;
import org.iconic.project.search.io.SearchExecutor;

import java.util.ArrayList;

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
                        getDatasetModel().get().getDataManager().getFeatureSize() - 1
                );
        supplier.addFunction(new ArrayList<>(getPrimitives().keySet()));

        EvolutionaryAlgorithm<ExpressionChromosome<Double>, Double> ea =
                new GeneExpressionProgramming<>(supplier);
        ea.addMutator(new ExpressionMutator<>());
        ea.addCrossover(new SimpleExpressionCrossover<>());
        ea.setCrossoverProbability(getCrossoverRate());
        ea.setMutationProbability(getMutationRate());
        ea.setObjective(
                new CacheableObjective<>(
                        new DefaultObjective(
                                new MeanSquaredError(), getDatasetModel().get().getDataManager()
                        )
                )
        );
        ea.initialisePopulation(getPopulationSize());

        SearchExecutor<ExpressionChromosome<Double>> searchExecutor =
                new SearchExecutor<>(getDatasetModel().get(), getEnabledPrimitives(), getNumGenerations());
        searchExecutor.setEvolutionaryAlgorithm(ea);

        return searchExecutor;
    }

    @Override
    protected boolean isValid() {
        return getDatasetModel().isPresent();
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
