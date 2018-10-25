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

import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.scene.control.Control;
import javafx.util.converter.NumberStringConverter;
import lombok.NonNull;
import org.controlsfx.glyphfont.FontAwesome;
import org.iconic.ea.operator.primitive.*;
import org.iconic.project.Displayable;
import org.iconic.project.dataset.DatasetModel;
import org.iconic.project.search.io.SearchExecutor;

import java.util.*;
import java.util.stream.Collectors;

/**
 * A SearchConfigurationModel exposes a collection of observable properties that are used to construct a
 * {@link org.iconic.project.search.io.SearchExecutor SearchExecutor}.
 */
public abstract class SearchConfigurationModel implements Displayable {
    private final Map<FunctionalPrimitive<Double, Double>, SimpleBooleanProperty> primitives;
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
     * Constructs a new search configuration with the provided name.
     *
     * @param name The name of the search configuration.
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
        this.primitives = new LinkedHashMap<>();
        primitives.put(new Addition(), new SimpleBooleanProperty(true));
        primitives.put(new Subtraction(), new SimpleBooleanProperty(true));
        primitives.put(new Multiplication(), new SimpleBooleanProperty(true));
        primitives.put(new Division(), new SimpleBooleanProperty(true));
        primitives.put(new Negation(), new SimpleBooleanProperty(true));

        primitives.put(new Cos(), new SimpleBooleanProperty(true));
        primitives.put(new Sin(), new SimpleBooleanProperty(true));
        primitives.put(new Tan(), new SimpleBooleanProperty(true));
        primitives.put(new ArcCos(), new SimpleBooleanProperty(true));
        primitives.put(new ArcSin(), new SimpleBooleanProperty(true));
        primitives.put(new ArcTan(), new SimpleBooleanProperty(true));
        primitives.put(new TwoArcTan(), new SimpleBooleanProperty(true));

        primitives.put(new Exponential(), new SimpleBooleanProperty(true));
        primitives.put(new NaturalLog(), new SimpleBooleanProperty(true));
        primitives.put(new Power(), new SimpleBooleanProperty(true));
        primitives.put(new SquareRoot(), new SimpleBooleanProperty(true));
        primitives.put(new Root(), new SimpleBooleanProperty(true));

        primitives.put(new LogisticFunction(), new SimpleBooleanProperty(true));
        primitives.put(new StepFunction(), new SimpleBooleanProperty(true));
        primitives.put(new SignFunction(), new SimpleBooleanProperty(true));
        primitives.put(new GaussianFunction(), new SimpleBooleanProperty(true));
        primitives.put(new Tanh(), new SimpleBooleanProperty(true));

        primitives.put(new EqualTo(), new SimpleBooleanProperty(true));
        primitives.put(new LessThan(), new SimpleBooleanProperty(true));
        primitives.put(new LessThanOrEqual(), new SimpleBooleanProperty(true));
        primitives.put(new GreaterThan(), new SimpleBooleanProperty(true));
        primitives.put(new GreaterThanOrEqual(), new SimpleBooleanProperty(true));
        primitives.put(new IfThenElse(), new SimpleBooleanProperty(true));
        primitives.put(new And(), new SimpleBooleanProperty(true));
        primitives.put(new Or(), new SimpleBooleanProperty(true));
        primitives.put(new Xor(), new SimpleBooleanProperty(true));
        primitives.put(new Not(), new SimpleBooleanProperty(true));

        primitives.put(new Minimum(), new SimpleBooleanProperty(true));
        primitives.put(new Maximum(), new SimpleBooleanProperty(true));
        primitives.put(new Modulo(), new SimpleBooleanProperty(true));
        primitives.put(new Floor(), new SimpleBooleanProperty(true));
        primitives.put(new Ceiling(), new SimpleBooleanProperty(true));
        primitives.put(new AbsoluteValue(), new SimpleBooleanProperty(true));

        // When a property is changed set the state of the configuration to changed
        this.populationSizeProperty().addListener(obs -> setChanged(true));
    }

    /**
     * Builds a search executor using this search configuration model.
     *
     * @return A search executor configured based on the search configuration model.
     * @see org.iconic.project.search.io.SearchExecutor
     */
    protected abstract SearchExecutor<?> buildSearchExecutor();

    /**
     * Bidirectionally binds the provided numeric property to a specified text property.
     *
     * @param property The numeric property to bind.
     * @param field    The text property to bind.
     */
    protected static void bindTextProperty(
            final Property<Number> property,
            final StringProperty field
    ) {
        Bindings.bindBidirectional(
                field,
                property,
                new NumberStringConverter()
        );
    }

    /**
     * Disable the specified control if it has no options select.
     *
     * @param control The control to disable.
     * @param options The options available to the control.
     */
    protected static void disableControlIfEmpty(final Control control, Collection<?> options) {
        if (options.size() < 1) {
            control.setDisable(true);
        } else {
            control.setDisable(false);
        }
    }

    /**
     * @return The name of the search configuration.
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
     */
    @Override
    public Optional<Enum<?>> getIcon() {
        return Optional.of(FontAwesome.Glyph.SEARCH);
    }

    /**
     * @return The universally unique identifier of the search configuration.
     */
    public UUID getId() {
        return id;
    }

    /**
     * @return The dataset model associated with the search configuration.
     */
    public Optional<DatasetModel> getDatasetModel() {
        return (datasetModel == null) ? Optional.empty() : Optional.of(datasetModel);
    }

    /**
     * @return The search executor associated with the search configuration.
     */
    public Optional<SearchExecutor<?>> getSearchExecutor() {
        if (isChanged()) {
            // Ensure that any pre-existing search executor is stopped before discarding its reference
            if (searchExecutor != null) {
                searchExecutor.stop();
            }
            setSearchExecutor(buildSearchExecutor());
        }

        return (searchExecutor != null && isValid()) ? Optional.of(searchExecutor) : Optional.empty();
    }

    /**
     * @return The mutation rate associated with the search configuration.
     */
    public double getMutationRate() {
        return mutationRate.get();
    }

    /**
     * @return The crossover rate associated with the search configuration.
     */
    public double getCrossoverRate() {
        return crossoverRate.get();
    }

    /**
     * @return The number of generations associated with the search configuration.
     */
    public int getNumGenerations() {
        return numGenerations.get();
    }

    /**
     * @return The population size associated with the search configuration.
     */
    public int getPopulationSize() {
        return populationSize.get();
    }

    /**
     * Returns true if this search configuration is in a valid state for constructing a
     * {@link org.iconic.project.search.io.SearchExecutor SearchExecutor}.
     *
     * @return True if the search configuration is valid.
     */
    protected abstract boolean isValid();

    /**
     * @return True if the search configuration hasn't been reevaluated since its last modification.
     */
    public boolean isChanged() {
        return changed;
    }

    /**
     * @return The primitives available to this search configuration.
     */
    public Map<FunctionalPrimitive<Double, Double>, SimpleBooleanProperty> getPrimitives() {
        return primitives;
    }

    /**
     * @return The primitives available to this search configuration that have been enabled for use.
     */
    public List<FunctionalPrimitive<Double, Double>> getEnabledPrimitives() {
        return primitives.keySet().stream().filter(e -> primitives.get(e).get()).collect(Collectors.toList());
    }

    /**
     * Sets the name of this search configuration to the provided value.
     *
     * @param name A non-empty string.
     */
    public void setName(final String name) {
        this.nameProperty().set(name);
    }

    /**
     * Sets the search executor associated with this search configuration.
     *
     * @param searchExecutor The search executor to associate with the search configuration.
     */
    public void setSearchExecutor(SearchExecutor<?> searchExecutor) {
        this.searchExecutor = searchExecutor;
    }

    /**
     * Sets the dataset model associated with this search configuration.
     *
     * @param datasetModel The dataset model to associate with the search configuration.
     */
    public void setDatasetModel(DatasetModel datasetModel) {
        setChanged(true);
        this.datasetModel = datasetModel;
    }

    /**
     * Sets the changed status of this search configuration.
     *
     * @param changed If true this configuration model will be updated the next time it's evaluated.
     */
    public void setChanged(boolean changed) {
        this.changed = changed;
    }

    /**
     * @return The name of the search configuration.
     */
    public final SimpleStringProperty nameProperty() {
        return name;
    }

    /**
     * @return The mutation rate of the search configuration.
     */
    public SimpleDoubleProperty mutationRateProperty() {
        return mutationRate;
    }

    /**
     * @return The crossover rate of the search configuration.
     */
    public SimpleDoubleProperty crossoverRateProperty() {
        return crossoverRate;
    }

    /**
     * @return The number of generations of the search configuration.
     */
    public SimpleIntegerProperty numGenerationsProperty() {
        return numGenerations;
    }

    /**
     * @return The population size of the search configuration.
     */
    public SimpleIntegerProperty populationSizeProperty() {
        return populationSize;
    }
}
