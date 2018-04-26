package org.iconic.workspace.console;

import com.google.inject.Inject;
import javafx.collections.MapChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import org.iconic.project.search.SearchModel;
import org.iconic.project.search.SearchService;

import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * A controller for handling the console and it's output.
 * </p>
 * <p>
 * The ConsoleController maintains a tab pane of console windows for searches to log their results onto.
 * </p>
 */
@Log4j2
public class ConsoleController implements Initializable {
    private final SearchService searchService;

    @FXML
    private TabPane consoleTabs;

    /**
     * <p>
     * Constructs a new ConsoleController that attaches a change listener onto the search service.
     * </p>
     */
    @Inject
    public ConsoleController(final SearchService searchService) {
        this.searchService = searchService;

        // Update the console whenever the searches change
        MapChangeListener<UUID, SearchModel> searchChangeListener = change -> {
            // Check the console tab pane as there's no guarantee it will exist when this is triggered
            if (this.consoleTabs == null) {
                return;
            }

            // If a search was added we'll first check if it already exists in the tab pane -
            // this can happen if a duplicate is added which will trigger an addition and removal (update) operation
            if (change.wasAdded() && change.getKey() != null && change.getValueAdded() != null) {
                final List<Tab> tabs = consoleTabs.getTabs().filtered(
                        t -> t.getId().equals(change.getKey().toString())
                );

                // If this isn't an update operation, add a new tab - but first make sure everything's not null
                if (tabs.size() < 1 && change.getValueAdded().getDatasetModel() != null) {
                    val newTab = new Tab();
                    newTab.setText(change.getValueAdded().getDatasetModel().getLabel());
                    // Set the ID so we can modify the tab pane later without re-rendering the entire thing
                    newTab.setId(change.getKey().toString());
                    this.consoleTabs.getTabs().add(newTab);
                }
                // Otherwise don't do anything - but for future reference, this may be undesirable if we want to change
                // tab names
            }

            // If a search was removed then remove the corresponding tab by comparing IDs
            if (change.wasRemoved() && change.getKey() != null) {
                this.consoleTabs.getTabs().removeIf(t -> t.getId().equals(change.getKey().toString()));
            }
        };

        getSearchService().searchesProperty().addListener(searchChangeListener);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Any user interface configuration that needs to happen at construction time must be done in this method to
     * guarantee that it's run after the user interface has been initialised.
     * </p>
     */
    @Override
    public void initialize(URL arg1, ResourceBundle arg2) {
        val searches = getSearchService().searchesProperty();

        // Check that the console tab pane actually exists
        if (consoleTabs != null) {
            // Add every search as a new tab to the pane
            final List<Tab> s = searches.entrySet().stream().map(model -> {
                val tab = new Tab();
                tab.setText(model.getValue().getDatasetModel().getLabel());
                // Set the ID so we can modify the tab pane later without re-rendering the entire thing
                tab.setId(model.getKey().toString());
                return tab;
            }).collect(Collectors.toList());

            consoleTabs.getTabs().setAll(s);
        }
    }

    /**
     * <p>
     * Returns the search service of this controller
     * </p>
     *
     * @return the search service of the controller
     */
    private SearchService getSearchService() {
        return searchService;
    }
}
