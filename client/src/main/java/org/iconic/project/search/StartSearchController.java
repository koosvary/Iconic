package org.iconic.project.search;

import com.google.inject.Inject;
import javafx.beans.InvalidationListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.control.Button;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import org.iconic.project.Displayable;
import org.iconic.project.dataset.DatasetModel;
import org.iconic.project.definition.DefineSearchService;
import org.iconic.workspace.WorkspaceService;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * A controller class for handling the StartSearch view.
 *
 * The StartSearchController provides functionality for starting and stopping current searches, whilst
 * also updating GUI information.
 */
@Log4j2
public class StartSearchController implements Initializable {
    private final SearchService searchService;
    private final WorkspaceService workspaceService;
    private final DefineSearchService defineSearchService;

    @FXML
    private Button btnSearch;
    @FXML
    private Button btnStopSearch;
    @FXML
    private LineChart<Number, Number> lcSearchProgress;

    /**
     * <p>
     * Constructs a new StartSearchController that attaches an invalidation listener onto the search and workspace
     * services.
     * </p>
     */
    @Inject
    public StartSearchController(final WorkspaceService workspaceService, final SearchService searchService, final DefineSearchService defineSearchService) {
        this.defineSearchService = defineSearchService;
        this.searchService = searchService;
        this.workspaceService = workspaceService;

        // Update the workspace whenever the active dataset changes
        InvalidationListener selectionChangedListener = observable -> updateWorkspace();
        getWorkspaceService().activeWorkspaceItemProperty().addListener(selectionChangedListener);
        getSearchService().searchesProperty().addListener(selectionChangedListener);
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

        // If no dataset is selected clear the UI
        if (!(item instanceof DatasetModel)) {
            clearUI();
        }

        // Make sure that all the UI elements actually exist
        if (btnSearch != null && btnStopSearch != null) {
            // If the selected item is a dataset
            if (item instanceof DatasetModel) {
                DatasetModel dataset = (DatasetModel) item;
                // Check if a search on the current active dataset is being performed
                SearchModel search = getSearchService().searchesProperty().get(dataset.getId());

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
    }

    /**
     * Starts a search using the currently selected dataset.
     *
     * @param actionEvent The action that triggered this event
     */
    public void startSearch(ActionEvent actionEvent) {
        Displayable item = getWorkspaceService().getActiveWorkspaceItem();

        // Check that there's an active dataset before starting the search
        if (item instanceof DatasetModel) {
            // TODO(Meyer): Use the function defined to determine what data is used, and what to calculate to
            log.info("Function for use: " + defineSearchService.getFunction());

            DatasetModel dataset = (DatasetModel) item;
            SearchModel search = getSearchService().searchesProperty().get(dataset.getId());

            // If there's no search already being performed on the dataset, start a new one
            if (search == null) {
                SearchModel newSearch = defineSearchService.getSearchModel(dataset);
                getSearchService().searchesProperty().put(dataset.getId(), newSearch);
                Thread thread = new Thread(getSearchService().searchesProperty().get(dataset.getId()));
                thread.start();
            }
            // Otherwise stop the current search
            else {
//              TODO implement pause functionality
                stopSearch(actionEvent);
            }
        }
    }

    /**
     * Stops the current search.
     *
     * @param actionEvent The action that triggered this event
     */
    public void stopSearch(ActionEvent actionEvent) {
        Displayable item = getWorkspaceService().getActiveWorkspaceItem();

        // Check that there's an active dataset before starting the search
        if (item instanceof DatasetModel) {
            val dataset = (DatasetModel) item;
            val search = getSearchService().searchesProperty().get(dataset.getId());

            lcSearchProgress.getData().clear();
            lcSearchProgress.getData().add(search.getPlots());

            search.stop();
            getSearchService().searchesProperty().remove(dataset.getId());
        }
    }

    /**
     * Clears the search graphs.
     */
    private void clearUI() {
        // Make sure the UI element actually exists
        if (lcSearchProgress != null) {
            lcSearchProgress.getData().clear();
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
