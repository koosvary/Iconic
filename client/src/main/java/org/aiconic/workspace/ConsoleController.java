package org.aiconic.workspace;

import javafx.collections.MapChangeListener;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import org.aiconic.model.GlobalModel;
import org.aiconic.model.SearchModel;

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
public class ConsoleController implements Initializable {
    @FXML
    private TabPane consoleTabs;

    /**
     * <p>
     * Constructs a new ConsoleController that attaches a change listener onto the global model.
     * </p>
     */
    public ConsoleController() {
        // Update the console whenever the searches change
        MapChangeListener<UUID, SearchModel> searchChangeListener = change -> {
            // Check the console tab pane as there's no guarantee it will exist when this is triggered
            if (this.consoleTabs == null) {
                return;
            }

            // If a search was added we'll first check if it already exists in the tab pane -
            // this can happen if a duplicate is added which will trigger an addition and removal (update) operation
            if (change.wasAdded() && change.getKey() != null && change.getValueAdded() != null) {
                final FilteredList<Tab> tabs = consoleTabs.getTabs().filtered(
                        t -> t.getId().equals(change.getKey().toString())
                );

                // If this isn't an update operation, add a new tab - but first make sure everything's not null
                if (tabs.size() < 1 && change.getValueAdded().getDatasetModel() != null) {
                    Tab newTab = new Tab();
                    newTab.setText(change.getValueAdded().getDatasetModel().getName());
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

        GlobalModel.INSTANCE.searchesProperty().addListener(searchChangeListener);
    }

    /**
     * <p>
     * Any user interface configuration that needs to happen at construction time must be done in this method to
     * guarantee that it's run after the user interface has been initialised.
     * </p>
     *
     * @param arg1
     * @param arg2
     */
    @Override
    public void initialize(URL arg1, ResourceBundle arg2) {
        final Map<UUID, SearchModel> searches =
                GlobalModel.INSTANCE.searchesProperty();

        // Check that the console tab pane actually exists
        if (consoleTabs != null) {
            // Add every search as a new tab to the pane
            List<Tab> s = searches.entrySet().stream().map(model -> {
                Tab tab = new Tab();
                tab.setText(model.getValue().getDatasetModel().getName());
                // Set the ID so we can modify the tab pane later without re-rendering the entire thing
                tab.setId(model.getKey().toString());
                return tab;
            }).collect(Collectors.toList());

            consoleTabs.getTabs().setAll(s);
        }
    }
}
