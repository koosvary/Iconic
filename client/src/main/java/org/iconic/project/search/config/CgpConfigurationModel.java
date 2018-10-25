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
import org.iconic.ea.strategies.EvolutionaryAlgorithm;
import org.iconic.ea.chromosome.cartesian.CartesianChromosome;
import org.iconic.ea.chromosome.cartesian.CartesianChromosomeFactory;
import org.iconic.ea.operator.evolutionary.mutation.cgp.CartesianSingleActiveMutator;
import org.iconic.ea.operator.objective.CacheableObjective;
import org.iconic.ea.operator.objective.DefaultObjective;
import org.iconic.ea.operator.objective.error.MeanSquaredError;
import org.iconic.ea.strategies.cgp.CartesianGeneticProgramming;
import org.iconic.project.search.io.SearchExecutor;

import java.util.ArrayList;
import java.util.List;

/**
 * {@inheritDoc}
 * <p>
 * A search configuration model designed specifically for a Cartesian Genetic Programming strategy.
 *
 * @see org.iconic.ea.strategies.cgp.CartesianGeneticProgramming
 */
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

    /**
     * {@inheritDoc}
     */
    @Override
    protected SearchExecutor<?> buildSearchExecutor() {
        setChanged(false);

        if (!isValid()) {
            return null;
        }

        List<String> inputs = new ArrayList<>(
                getDatasetModel().get().getDataManager().getDataset().keySet()
        );
        inputs.remove(inputs.size() - 1);

        CartesianChromosomeFactory<Double> supplier =
                new CartesianChromosomeFactory<>(
                        getNumOutputs(), inputs,
                        getNumColumns(), getNumRows(), getNumLevelsBack()
                );
        supplier.addFunction(new ArrayList<>(getEnabledPrimitives()));

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
                new SearchExecutor<>(getDatasetModel().get(), getEnabledPrimitives(), this);
        searchExecutor.setEvolutionaryAlgorithm(ea);

        return searchExecutor;
    }

    /**
     * @return The number of outputs associated with the search configuration.
     */
    public int getNumOutputs() {
        return numOutputs.get();
    }

    /**
     * @return The number of columns associated with the search configuration.
     */
    public int getNumColumns() {
        return numColumns.get();
    }

    /**
     * @return The number of rows associated with the search configuration.
     */
    public int getNumRows() {
        return numRows.get();
    }

    /**
     * @return The number of levels back associated with the search configuration.
     */
    public int getNumLevelsBack() {
        return numLevelsBack.get();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean isValid() {
        return getDatasetModel().isPresent();
    }

    /**
     * @return The number of outputs of the search configuration.
     */
    public SimpleIntegerProperty numOutputsProperty() {
        return numOutputs;
    }

    /**
     * @return The number of columns of the search configuration.
     */
    public SimpleIntegerProperty numColumnsProperty() {
        return numColumns;
    }

    /**
     * @return The number of rows of the search configuration.
     */
    public SimpleIntegerProperty numRowsProperty() {
        return numRows;
    }

    /**
     * @return The number of levels back of the search configuration.
     */
    public SimpleIntegerProperty numLevelsBackProperty() {
        return numLevelsBack;
    }
}
