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

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import lombok.NonNull;
import org.controlsfx.glyphfont.FontAwesome;
import org.iconic.ea.operator.primitive.*;
import org.iconic.project.Displayable;
import org.iconic.project.dataset.DatasetModel;
import org.iconic.project.search.io.SearchExecutor;

import java.util.*;
import java.util.stream.Collectors;

public abstract class SearchConfigurationModel implements Displayable {
    private final Map<FunctionalPrimitive<Double, Double>, Boolean> primitives;
    private final UUID id;
    private final SimpleStringProperty name;
    private SimpleIntegerProperty populationSize;
    private SimpleIntegerProperty numGenerations;
    private SimpleDoubleProperty mutationRate;
    private SimpleDoubleProperty crossoverRate;
    private DatasetModel datasetModel;
    private SearchExecutor<?> searchExecutor;
    private boolean changed;

    /**
     * <p>Constructs a new search configuration with the provided name.</p>
     *
     * @param name The name of the search configuration
     */
    public SearchConfigurationModel(@NonNull final String name) {
        this.changed = true;
        this.name = new SimpleStringProperty(name);
        this.id = UUID.randomUUID();
        this.populationSize = new SimpleIntegerProperty(100);
        this.numGenerations = new SimpleIntegerProperty(0);
        this.mutationRate = new SimpleDoubleProperty(0.1);
        this.crossoverRate = new SimpleDoubleProperty(0.1);
        this.datasetModel = null;
        this.primitives = new HashMap<>();
        primitives.put(new AbsoluteValue(), true);
        primitives.put(new Addition(), true);
        primitives.put(new And(), true);
        primitives.put(new ArcCos(), true);
        primitives.put(new ArcSin(), true);
        primitives.put(new ArcTan(), true);
        primitives.put(new Ceiling(), true);
        primitives.put(new Cos(), true);
        primitives.put(new Division(), true);
        primitives.put(new EqualTo(), true);
        primitives.put(new Exponential(), true);
        primitives.put(new Floor(), true);
        primitives.put(new GaussianFunction(), true);
        primitives.put(new GreaterThan(), true);
        primitives.put(new GreaterThanOrEqual(), true);
        primitives.put(new IfThenElse(), true);
        primitives.put(new LessThan(), true);
        primitives.put(new LessThanOrEqual(), true);
        primitives.put(new LogisticFunction(), true);
        primitives.put(new Maximum(), true);
        primitives.put(new Minimum(), true);
        primitives.put(new Modulo(), true);
        primitives.put(new Multiplication(), true);
        primitives.put(new NaturalLog(), true);
        primitives.put(new Negation(), true);
        primitives.put(new Not(), true);
        primitives.put(new Or(), true);
        primitives.put(new Power(), true);
        primitives.put(new Root(), true);
        primitives.put(new SignFunction(), true);
        primitives.put(new Sin(), true);
        primitives.put(new SquareRoot(), true);
        primitives.put(new StepFunction(), true);
        primitives.put(new Subtraction(), true);
        primitives.put(new Tan(), true);
        primitives.put(new Tanh(), true);
        primitives.put(new TwoArcTan(), true);
        primitives.put(new Xor(), true);
    }

    /**
     * Returns the name of this search configuration
     *
     * @return the name of the search configuration
     */
    public String getName() {
        return nameProperty().get();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getLabel() {
        return getName();
    }

    /**
     * {@inheritDoc}
     *
     * @return
     */
    @Override
    public Optional<Enum<?>> getIcon() {
        return Optional.of(FontAwesome.Glyph.SEARCH);
    }

    /**
     * <p>Returns the name property of this dataset.</p>
     *
     * @return The name property of the dataset
     */
    public final SimpleStringProperty nameProperty() {
        return name;
    }

    /**
     * <p>Sets the name of this search configuration to the provided value.</p>
     *
     * @param name The new name for this search configuration
     */
    public void setName(final String name) {
        this.nameProperty().set(name);
    }

    /**
     * <p>Returns the UUID of this search configuration.</p>
     *
     * @return The universally unique identifier of the search configuration
     */
    public UUID getId() {
        return id;
    }

    /**
     * <p>Returns the dataset model associated with this search configuration.</p>
     *
     * @return The dataset model associated with the search configuration
     */
    public Optional<DatasetModel> getDatasetModel() {
        return (datasetModel == null) ? Optional.empty() : Optional.of(datasetModel);
    }

    public void setDatasetModel(DatasetModel datasetModel) {
        setChanged(true);
        this.datasetModel = datasetModel;
    }

    /**
     * <p>Returns the search executor associated with this search configuration.</p>
     *
     * @return The search executor associated with the search configuration
     */
    public Optional<SearchExecutor<?>> getSearchExecutor() {
        if (isChanged()) {
            if (searchExecutor != null) {
                searchExecutor.stop();
            }
            setSearchExecutor(buildSearchExecutor());
        }

        return (searchExecutor != null && isValid()) ? Optional.of(searchExecutor) : Optional.empty();
    }

    public void setSearchExecutor(SearchExecutor<?> searchExecutor) {
        this.searchExecutor = searchExecutor;
    }

    protected abstract SearchExecutor<?> buildSearchExecutor();

    public Map<FunctionalPrimitive<Double, Double>, Boolean> getPrimitives() {
        return primitives;
    }

    public List<FunctionalPrimitive<Double, Double>> getEnabledPrimitives() {
        return primitives.keySet().stream().filter(primitives::get).collect(Collectors.toList());
    }

    public double getMutationRate() {
        return mutationRate.get();
    }

    public SimpleDoubleProperty mutationRateProperty() {
        return mutationRate;
    }

    public void setMutationRate(double mutationRate) {
        setChanged(true);
        this.mutationRate.set(mutationRate);
    }

    public double getCrossoverRate() {
        return crossoverRate.get();
    }

    public SimpleDoubleProperty crossoverRateProperty() {
        return crossoverRate;
    }

    public void setCrossoverRate(double crossoverRate) {
        setChanged(true);
        this.crossoverRate.set(crossoverRate);
    }

    protected abstract boolean isValid();

    protected boolean isChanged() {
        return changed;
    }

    protected void setChanged(boolean changed) {
        this.changed = changed;
    }

    public int getNumGenerations() {
        return numGenerations.get();
    }

    public SimpleIntegerProperty numGenerationsProperty() {
        return numGenerations;
    }

    public void setNumGenerations(int numGenerations) {
        setChanged(true);
        this.numGenerations.set(numGenerations);
    }

    public int getPopulationSize() {
        return populationSize.get();
    }

    public SimpleIntegerProperty populationSizeProperty() {
        return populationSize;
    }

    public void setPopulationSize(int populationSize) {
        setChanged(true);
        this.populationSize.set(populationSize);
    }
}
