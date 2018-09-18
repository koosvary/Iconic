package org.iconic.project.search.config;

import javafx.beans.property.SimpleStringProperty;
import lombok.NonNull;
import org.controlsfx.glyphfont.FontAwesome;
import org.iconic.project.Displayable;
import org.iconic.project.dataset.DatasetModel;
import org.iconic.project.search.io.SearchExecutor;

import java.util.Optional;
import java.util.UUID;

public class SearchConfigurationModel implements Displayable {
    private final UUID id;
    private final SimpleStringProperty name;
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
}
