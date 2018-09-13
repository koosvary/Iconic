package org.iconic.workspace;

import com.google.inject.Inject;
import javafx.beans.InvalidationListener;
import javafx.fxml.Initializable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import org.iconic.control.WorkspaceTab;
import org.iconic.project.Displayable;
import org.iconic.project.dataset.DatasetModel;

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
    @FXML
    @FXML
    private Tab tbInputDataView;
    private Tab tbProcessDataView;
    @FXML
    private Tab tbDefineSearchView;
    @FXML
    @FXML
    private Tab tbStartSearchView;
    private Tab tbResultsView;
    @FXML
    private Tab tbReportView;
    @Getter(AccessLevel.PRIVATE)
    private final String defaultName;

    @Getter(AccessLevel.PRIVATE)
    private final String defaultWelcomeMessage;

    /**
     * <p>
     * Constructs a new WorkspaceController that attaches an invalidation listener onto the workspace service.
     * </p>
     */
    @Inject
    public WorkspaceController(final WorkspaceService workspaceService) {
        this.workspaceService = workspaceService;
        this.defaultName = "";
        this.defaultWelcomeMessage = "Select a dataset on the left to get started.";

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
        val item = getWorkspaceService().getActiveWorkspaceItem();

        // If no dataset is selected clear the UI of dataset related elements
        // TODO: if a search is selected, do not clear the results
        if (!(item instanceof DatasetModel)) {
            clearUI();
        }

        if (item instanceof DatasetModel) {
            updateAvailableTabs(WorkspaceTab.TabType.DATASET);
        } else if (item instanceof SearchConfigurationModel) {
            updateAvailableTabs(WorkspaceTab.TabType.SEARCH);
        } else {
//            updateAvailableTabs(WorkspaceTab.TabType.OTHER);
        }
        // Otherwise a dataset is selected, so we need to ensure that any non-dataset related UI elements
        // are disabled, and vice-versa

        // Make sure that all the UI elements actually exist
        if (btnSearch != null && btnStopSearch != null) {
            // If the selected item is a dataset
            if (item instanceof DatasetModel) {
                DatasetModel dataset = (DatasetModel) item;
                // Check if a search on the current active dataset is being performed
                SearchExecutor search = getSearchService().searchesProperty().get(dataset.getId());

                // If there's no search...
                if (search == null) {
                    btnSearch.setText("Start Search");
                    btnSearch.setDisable(false);
                    btnStopSearch.setDisable(true);
                    btnStopSearch.setVisible(false);
                }
                // Otherwise...
                else {
                    btnSearch.setText("Pause");
                    btnSearch.setDisable(true);
                    btnStopSearch.setDisable(false);
                    btnStopSearch.setVisible(true);
                }
            }
            // Otherwise if no interesting project item is selected
            else {
                // Display some default messages
                btnSearch.setText("Start Search");
                btnStopSearch.setVisible(false);
                // And disable the search buttons
                btnSearch.setDisable(true);
                btnStopSearch.setDisable(true);
            }
        }

        //  Make sure the UI element actually exist
        if (lvFeatures != null) {
            // If the selected item is a dataset
            if (item instanceof DatasetModel) {
                // Add Dataset Headers to list
                Optional<DataManager<Double>> dataManager = getDataManager();

                if (dataManager.isPresent()) {
                    ObservableList<String> items = FXCollections.observableArrayList(dataManager.get().getSampleHeaders());
                    lvFeatures.setItems(items);
                }
            }
            // Otherwise clear the elements in the table
            else {
                lvFeatures.getItems().clear();
            }
        }
    }

    /**
     * <p>Update the tabs that are available for selection based on the provided tab type</p>
     *
     * @param availableTabs The tab types to enable, tabs of other types will be disabled
     */
    private void updateAvailableTabs(WorkspaceTab.TabType availableTabs) {
        if (tbpWorkspace != null) {
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
            updateTabSelection();
        }
    }

    /**
     * <p>Update the tab selection model if the user has a disabled tab selected</p>
     *
     * <p>The selection model will default to the first enabled tab if available, otherwise no tab will be selected</p>
     */
    private void updateTabSelection() {
        SingleSelectionModel<Tab> selectionModel = tbpWorkspace.getSelectionModel();
        Tab selectedTab = selectionModel.getSelectedItem();

        if (selectedTab.isDisable()) {
            // Clear the user's selection if their selected tab is disabled
            selectionModel.clearSelection();
            FilteredList<Tab> filteredTabs = tbpWorkspace.getTabs().filtered(Tab::isDisable);

            if (filteredTabs.size() > 0) {
                Tab newSelection = filteredTabs.get(0);
                tbpWorkspace.getSelectionModel().select(newSelection);
            }
        }
    }

    /**
     * <p>Clear all context-specific UI data</p>
     */
    /**
     * Clears the search graphs.
     */
    private void clearUI() {
        // To add later
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