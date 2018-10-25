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

import com.google.inject.Inject;
import javafx.beans.property.SimpleIntegerProperty;
import lombok.NonNull;
import org.iconic.ea.strategies.EvolutionaryAlgorithm;
import org.iconic.ea.chromosome.expression.ExpressionChromosome;
import org.iconic.ea.chromosome.expression.ExpressionChromosomeFactory;
import org.iconic.ea.operator.evolutionary.crossover.gep.SimpleExpressionCrossover;
import org.iconic.ea.operator.evolutionary.mutation.gep.ExpressionMutator;
import org.iconic.ea.operator.objective.CacheableObjective;
import org.iconic.ea.operator.objective.DefaultObjective;
import org.iconic.ea.operator.objective.error.MeanSquaredError;
import org.iconic.ea.operator.primitive.FunctionalPrimitive;
import org.iconic.ea.strategies.gep.GeneExpressionProgramming;
import org.iconic.project.search.io.SearchExecutor;
import org.iconic.reflection.ClassLoaderService;

import java.util.ArrayList;
import java.util.List;

/**
 * {@inheritDoc}
 * <p>
 * A search configuration model designed specifically for a Gene Expression Programming strategy.
 *
 * @see org.iconic.ea.strategies.gep.GeneExpressionProgramming
 */
public class GepConfigurationModel extends SearchConfigurationModel {
    private SimpleIntegerProperty headLength;

    /**
     * {@inheritDoc}
     */
    public GepConfigurationModel(
            @NonNull final String name,
            @NonNull final List<FunctionalPrimitive<Double, Double>> primitives
    ) {
        super(name, primitives);
        this.headLength = new SimpleIntegerProperty(5);
        this.headLengthProperty().addListener(obs -> setChanged(true));
    }

    /**
     * {@inheritDoc}
     */
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
                new SearchExecutor<>(getDatasetModel().get(), getEnabledPrimitives(), this);
        searchExecutor.setEvolutionaryAlgorithm(ea);

        return searchExecutor;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean isValid() {
        return getDatasetModel().isPresent();
    }

    /**
     * @return The head length associated with the search configuration.
     */
    public int getHeadLength() {
        return headLength.get();
    }

    /**
     * @return The head length of the search configuration.
     */
    public SimpleIntegerProperty headLengthProperty() {
        return headLength;
    }
}
