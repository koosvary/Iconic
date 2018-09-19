package org.iconic.workspace;

import com.google.inject.Inject;
import javafx.beans.InvalidationListener;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import lombok.extern.log4j.Log4j2;
import org.iconic.control.WorkspaceTab;
import org.iconic.project.Displayable;
import org.iconic.project.dataset.DatasetModel;
import org.iconic.project.search.config.SearchConfigurationModel;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * <p>
 * A controller for handling the workspace view.
 * </p>
 * <p>
 * The WorkspaceController maintains the information shown within the workspace based on the current active dataset.
 * </p>
 */
@Log4j2
public class WorkspaceController implements Initializable {
    private final WorkspaceService workspaceService;

    @FXML
    private TabPane tbpWorkspace;

    /**
     * <p>
     * Constructs a new WorkspaceController that attaches an invalidation listener onto the workspace service.
     * </p>
     */
    @Inject
    public WorkspaceController(final WorkspaceService workspaceService) {
        this.workspaceService = workspaceService;

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

        if (item instanceof DatasetModel) {
            updateAvailableTabs(WorkspaceTab.TabType.DATASET);
        } else if (item instanceof SearchConfigurationModel) {
            updateAvailableTabs(WorkspaceTab.TabType.SEARCH);
        } else {
            updateAvailableTabs(WorkspaceTab.TabType.OTHER);
        }
    }

    /**
     * <p>Update the tabs that are available for selection based on the provided tab type</p>
     *
     * @param availableTabs The tab types to enable, tabs of other types will be disabled
     */
    private void updateAvailableTabs(WorkspaceTab.TabType availableTabs) {
        if (tbpWorkspace == null) {
            return;
        }

        tbpWorkspace.getTabs().forEach(tab -> {
            WorkspaceTab wTab = (WorkspaceTab) tab;
            if (wTab.getTabType().equals(availableTabs)) {
                tab.setDisable(false);
            } else {
                tab.setDisable(true);
            }
        });

        // If the current selected tab is disabled, change the user's
        // selection to the first enabled tab if available
//        updateTabSelection();
    }

    /**
     * <p>Update the tab selection model if the user has a disabled tab selected</p>
     *
     * <p>The selection model will default to the first enabled tab if available, otherwise no tab will be selected</p>
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
     * <p>
     * Returns the workspace service of this controller
     * </p>
     *
     * @return the workspace service of the controller
     */
    private WorkspaceService getWorkspaceService() {
        return workspaceService;
    }
}