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
package org.iconic.workspace;

import com.google.inject.Inject;
import javafx.beans.InvalidationListener;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.iconic.control.WorkspaceTab;
import org.iconic.project.Displayable;
import org.iconic.project.dataset.DatasetModel;
import org.iconic.project.search.config.SearchConfigurationModel;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.List;

/**
 * <p>
 * A controller for handling the workspace view.
 *
 * <p>
 * The WorkspaceController maintains the information shown within the workspace based on the current active dataset.
 *
 */
@Log4j2
public class WorkspaceController implements Initializable {
    private final WorkspaceService workspaceService;

    @Getter(AccessLevel.PRIVATE)
    private final String defaultName;
    @FXML
    private TabPane tbpWorkspace;

    /**
     * <p>
     * Constructs a new WorkspaceController that attaches an invalidation listener onto the workspace service.
     *
     */
    @Inject
    public WorkspaceController(final WorkspaceService workspaceService) {
        this.workspaceService = workspaceService;
        this.defaultName = "";

        // Update the workspace whenever the active dataset changes
        InvalidationListener selectionChangedListener = observable -> updateWorkspace();
        getWorkspaceService().activeWorkspaceItemProperty().addListener(selectionChangedListener);
    }

    @Override
    public void initialize(URL arg1, ResourceBundle arg2) {
        updateWorkspace();
    }

    /**
     * Updates the workspace to match the current active dataset.
     */
    private void updateWorkspace() {
        Displayable item = getWorkspaceService().getActiveWorkspaceItem();
        List<WorkspaceTab.TabType> activeTabs = new ArrayList<>();

        if (item instanceof DatasetModel) {
            activeTabs.add(WorkspaceTab.TabType.DATASET);
            activeTabs.add(WorkspaceTab.TabType.INPUT);
        } else if (item instanceof SearchConfigurationModel) {
            activeTabs.add(WorkspaceTab.TabType.SEARCH);
        } else {
            activeTabs.add(WorkspaceTab.TabType.INPUT);
        }

        updateAvailableTabs(activeTabs);

        // If no dataset is selected clear the UI of dataset related elements
        // TODO: if a search is selected, do not clear the results
        if (!(item instanceof DatasetModel)) {
            clearUI();
        }
    }

    /**
     * <p>Update the tabs that are available for selection based on the provided tab types
     *
     * @param availableTabs The list of tab types to enable, tabs of other types will be disabled
     */
    private void updateAvailableTabs(List<WorkspaceTab.TabType> availableTabs) {
        if (tbpWorkspace == null) {
            return;
        }

        tbpWorkspace.getTabs().forEach(tab -> {
            WorkspaceTab wTab = (WorkspaceTab) tab;

            for (int i=0; i < availableTabs.size(); i++) {
                if (wTab.getTabType().equals(availableTabs.get(i)) || wTab.getTabType().equals(WorkspaceTab.TabType.ALL)) {
                    tab.setDisable(false);
                    break;
                } else {
                    tab.setDisable(true);
                }
            }
        });

        // If the current selected tab is disabled, change the user's selection to the first enabled tab if available
        //updateTabSelection();
    }

    /**
     * <p>Update the tab selection model if the user has a disabled tab selected
     *
     * <p>The selection model will default to the first enabled tab if available, otherwise no tab will be selected
     */
    private void updateTabSelection() {
        if (tbpWorkspace == null) {
            return;
        }

        SingleSelectionModel<Tab> selectionModel = tbpWorkspace.getSelectionModel();
        Tab selectedTab = selectionModel.getSelectedItem();

        if (selectedTab != null && selectedTab.isDisabled()) {
            // Clear the user's selection if their selected tab is disabled
            selectionModel.clearSelection();
            FilteredList<Tab> filteredTabs = tbpWorkspace.getTabs().filtered(t -> !t.isDisabled());

            if (filteredTabs.size() > 0) {
                Tab newSelection = filteredTabs.get(0);
                tbpWorkspace.getSelectionModel().select(newSelection);
            }
        }
    }

    /**
     * Clears the search graphs.
     */
    private void clearUI() {
        // To add later
    }

    /**
     * <p>
     * Returns the workspace service of this controller
     *
     *
     * @return the workspace service of the controller
     */
    private WorkspaceService getWorkspaceService() {
        return workspaceService;
    }
}