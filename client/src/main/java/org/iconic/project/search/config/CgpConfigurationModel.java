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
