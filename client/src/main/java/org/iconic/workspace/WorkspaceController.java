package org.iconic.workspace;

import com.google.inject.Inject;
import javafx.beans.InvalidationListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.val;
import org.iconic.project.Displayable;
import org.iconic.project.dataset.DatasetModel;
import org.iconic.project.search.SearchModel;
import org.iconic.project.search.SearchService;

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
public class WorkspaceController implements Initializable {
    private final WorkspaceService workspaceService;
    private final SearchService searchService;

    @FXML
    private Button btnSearch;
    @FXML
    private Button btnStopSearch;

    @Getter(AccessLevel.PRIVATE)
    private final String defaultName;

    @Getter(AccessLevel.PRIVATE)
    private final String defaultWelcomeMessage;

    /**
     * <p>
     * Constructs a new WorkspaceController that attaches an invalidation listener onto the search and workspace
     * services.
     * </p>
     */
    @Inject
    public WorkspaceController(final WorkspaceService workspaceService, final SearchService searchService) {
        this.workspaceService = workspaceService;
        this.searchService = searchService;
        this.defaultName = "";
        this.defaultWelcomeMessage = "Select a dataset on the left to get started.";

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
     * Starts a search using the currently selected dataset.
     *
     * @param actionEvent The action that triggered this event
     */
    public void startSearch(ActionEvent actionEvent) {
        Displayable item = getWorkspaceService().getActiveWorkspaceItem();

        // Check that there's an active dataset before starting the search
        if (item instanceof DatasetModel) {
            val dataset = (DatasetModel) item;
            val search = getSearchService().searchesProperty().get(dataset.getId());

            // If there's no search already being performed on the dataset, start a new one
            if (search == null) {
                SearchModel newSearch = new SearchModel(dataset);
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

            search.stop();
            getSearchService().searchesProperty().remove(dataset.getId());
        }
    }

    /**
     * Updates the workspace to match the current active dataset.
     */
    private void updateWorkspace() {
        val item = getWorkspaceService().getActiveWorkspaceItem();

        // Make sure that all the UI elements actually exist
        if (btnSearch != null && btnStopSearch != null) {
            // If the selected item is a dataset
            if (item instanceof DatasetModel) {
                val dataset = (DatasetModel) item;
                // Check if a search on the current active dataset is being performed
                val search = getSearchService().searchesProperty().get(dataset.getId());
                btnSearch.setDisable(false);

                // If there's an active search...
                if (search != null) {
                    btnSearch.setText("Pause");
                    btnSearch.setDisable(true);
                    btnStopSearch.setVisible(true);
                }
                // Otherwise...
                else {
                    btnSearch.setText("Start Search");
                    btnSearch.setDisable(false);
                    btnStopSearch.setVisible(false);
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