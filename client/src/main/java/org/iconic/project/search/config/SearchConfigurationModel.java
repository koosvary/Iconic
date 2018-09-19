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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public abstract class SearchConfigurationModel implements Displayable {
    private final List<FunctionalPrimitive<Double, Double>> primitives;
    private final UUID id;
    private final SimpleStringProperty name;
    private SimpleDoubleProperty mutationRate;
    private SimpleDoubleProperty crossoverRate;
    private DatasetModel datasetModel;
    private SearchExecutor searchExecutor;

    /**
     * <p>Constructs a new search configuration with the provided name.</p>
     *
     * @param name The name of the search configuration
     */
    public SearchConfigurationModel(@NonNull final String name) {
        this.name = new SimpleStringProperty(name);
        this.id = UUID.randomUUID();
        this.mutationRate = new SimpleDoubleProperty(0.1);
        this.crossoverRate = new SimpleDoubleProperty(0.1);

        this.primitives = new ArrayList<>();
        this.primitives.add(new AbsoluteValue());
        this.primitives.add(new Addition());
        this.primitives.add(new And());
        this.primitives.add(new ArcCos());
        this.primitives.add(new ArcSin());
        this.primitives.add(new ArcTan());
        this.primitives.add(new Ceiling());
        this.primitives.add(new Cos());
        this.primitives.add(new Division());
        this.primitives.add(new EqualTo());
        this.primitives.add(new Exponential());
        this.primitives.add(new Floor());
        this.primitives.add(new GaussianFunction());
        this.primitives.add(new GreaterThan());
        this.primitives.add(new GreaterThanOrEqual());
        this.primitives.add(new IfThenElse());
        this.primitives.add(new LessThan());
        this.primitives.add(new LessThanOrEqual());
        this.primitives.add(new LogisticFunction());
        this.primitives.add(new Maximum());
        this.primitives.add(new Minimum());
        this.primitives.add(new Modulo());
        this.primitives.add(new Multiplication());
        this.primitives.add(new NaturalLog());
        this.primitives.add(new Negation());
        this.primitives.add(new Not());
        this.primitives.add(new Or());
        this.primitives.add(new Power());
        this.primitives.add(new Root());
        this.primitives.add(new SignFunction());
        this.primitives.add(new Sin());
        this.primitives.add(new SquareRoot());
        this.primitives.add(new StepFunction());
        this.primitives.add(new Subtraction());
        this.primitives.add(new Tan());
        this.primitives.add(new Tanh());
        this.primitives.add(new TwoArcTan());
        this.primitives.add(new Xor());
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
    public DatasetModel getDatasetModel() {
        return datasetModel;
    }

    public void setDatasetModel(DatasetModel datasetModel) {
        this.datasetModel = datasetModel;
    }

    /**
     * <p>Returns the search executor associated with this search configuration.</p>
     *
     * @return The search executor associated with the search configuration
     */
    public Optional<SearchExecutor> getSearchExecutor() {
        return (searchExecutor != null) ? Optional.of(searchExecutor) : Optional.empty();
    }

    public void setSearchExecutor(SearchExecutor searchExecutor) {
        this.searchExecutor = searchExecutor;
    }

    public List<FunctionalPrimitive<Double, Double>> getPrimitives() {
        return primitives;
    }

    public double getMutationRate() {
        return mutationRate.get();
    }

    public SimpleDoubleProperty mutationRateProperty() {
        return mutationRate;
    }

    public void setMutationRate(double mutationRate) {
        this.mutationRate.set(mutationRate);
    }

    public double getCrossoverRate() {
        return crossoverRate.get();
    }

    public SimpleDoubleProperty crossoverRateProperty() {
        return crossoverRate;
    }

    public void setCrossoverRate(double crossoverRate) {
        this.crossoverRate.set(crossoverRate);
    }
}
