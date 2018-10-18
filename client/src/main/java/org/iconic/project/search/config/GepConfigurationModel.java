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
        this.headLength = new SimpleIntegerProperty(5);
        this.headLengthProperty().addListener(obs -> setChanged(true));
    }

    @Override
    protected SearchExecutor<?> buildSearchExecutor() {
        setChanged(false);

        if (!isValid()) {
            return null;
        }

        ExpressionChromosomeFactory<Double> supplier =
                new ExpressionChromosomeFactory<>(
                        getHeadLength(),
                        getDatasetModel().get().getDataManager().getFeatureSize() - 1
                );
        supplier.addFunction(new ArrayList<>(getEnabledPrimitives()));

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
