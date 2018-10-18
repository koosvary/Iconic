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
import org.iconic.ea.chromosome.cartesian.CartesianChromosome;
import org.iconic.ea.chromosome.cartesian.CartesianChromosomeFactory;
import org.iconic.ea.chromosome.expression.ExpressionChromosome;
import org.iconic.ea.chromosome.expression.ExpressionChromosomeFactory;
import org.iconic.ea.operator.evolutionary.crossover.gep.SimpleExpressionCrossover;
import org.iconic.ea.operator.evolutionary.mutation.cgp.CartesianSingleActiveMutator;
import org.iconic.ea.operator.evolutionary.mutation.gep.ExpressionMutator;
import org.iconic.ea.operator.objective.CacheableObjective;
import org.iconic.ea.operator.objective.DefaultObjective;
import org.iconic.ea.operator.objective.error.MeanSquaredError;
import org.iconic.ea.strategies.cgp.CartesianGeneticProgramming;
import org.iconic.ea.strategies.gep.GeneExpressionProgramming;
import org.iconic.project.search.io.SearchExecutor;

import java.util.ArrayList;

public class CgpConfigurationModel extends SearchConfigurationModel {
    private SimpleIntegerProperty numOutputs;
    private SimpleIntegerProperty numColumns;
    private SimpleIntegerProperty numRows;
    private SimpleIntegerProperty numLevelsBack;

    /**
     * {@inheritDoc}
     */
    public CgpConfigurationModel(@NonNull final String name) {
        super(name);
        this.numOutputs = new SimpleIntegerProperty(2);
        this.numColumns = new SimpleIntegerProperty(1);
        this.numRows = new SimpleIntegerProperty(1);
        this.numLevelsBack = new SimpleIntegerProperty(1);
        this.numOutputsProperty().addListener(obs -> setChanged(true));
        this.numColumnsProperty().addListener(obs -> setChanged(true));
        this.numRowsProperty().addListener(obs -> setChanged(true));
        this.numLevelsBackProperty().addListener(obs -> setChanged(true));
    }

    @Override
    protected SearchExecutor<?> buildSearchExecutor() {
        setChanged(false);

        if (!isValid()) {
            return null;
        }

        CartesianChromosomeFactory<Double> supplier =
                new CartesianChromosomeFactory<>(
                        getNumOutputs(), getDatasetModel().get().getDataManager().getFeatureSize() - 1,
                        getNumColumns(), getNumRows(), getNumLevelsBack()
                );
        supplier.addFunction(new ArrayList<>(getPrimitives().keySet()));

        EvolutionaryAlgorithm<CartesianChromosome<Double>, Double> ea =
                new CartesianGeneticProgramming<>(supplier);
        ea.addMutator(new CartesianSingleActiveMutator<>());
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

        SearchExecutor<CartesianChromosome<Double>> searchExecutor =
                new SearchExecutor<>(getDatasetModel().get(), getEnabledPrimitives(), getNumGenerations());
        searchExecutor.setEvolutionaryAlgorithm(ea);

        return searchExecutor;
    }

    @Override
    protected boolean isValid() {
        return getDatasetModel().isPresent();
    }

    public int getNumOutputs() {
        return numOutputs.get();
    }

    public SimpleIntegerProperty numOutputsProperty() {
        return numOutputs;
    }

    public void setNumOutputs(int numOutputs) {
        setChanged(true);
        this.numOutputs.set(numOutputs);
    }

    public int getNumColumns() {
        return numColumns.get();
    }

    public SimpleIntegerProperty numColumnsProperty() {
        return numColumns;
    }

    public void setNumColumns(int numColumns) {
        setChanged(true);
        this.numColumns.set(numColumns);
    }

    public int getNumRows() {
        return numRows.get();
    }

    public SimpleIntegerProperty numRowsProperty() {
        return numRows;
    }

    public void setNumRows(int numRows) {
        setChanged(true);
        this.numRows.set(numRows);
    }

    public int getNumLevelsBack() {
        return numLevelsBack.get();
    }

    public SimpleIntegerProperty numLevelsBackProperty() {
        return numLevelsBack;
    }

    public void setNumLevelsBack(int numLevelsBack) {
        setChanged(true);
        this.numLevelsBack.set(numLevelsBack);
    }
}
