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
import org.iconic.project.search.config.SearchConfigurationModel;
import org.iconic.project.search.io.SearchExecutor;
import org.iconic.views.ViewService;
import org.iconic.workspace.WorkspaceService;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

/**
 * A controller class for handling the StartSearch view.
 * <p>
 * The StartSearchController provides functionality for starting and stopping current searches, whilst
 * also updating GUI information.
 */
@Log4j2
public class StartSearchController implements Initializable {
    private final SearchService searchService;
    private final ViewService viewService;
    private final WorkspaceService workspaceService;

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
    public StartSearchController(
            final WorkspaceService workspaceService,
            final SearchService searchService,
            final ViewService viewService
    ) {
        this.searchService = searchService;
        this.viewService = viewService;
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
        Displayable item = getWorkspaceService().getActiveWorkspaceItem();

        if (!(item instanceof SearchConfigurationModel)) {
            return;
        }

        SearchConfigurationModel configModel = (SearchConfigurationModel) item;

        // Make sure that all the UI elements actually exist
        if (btnSearch != null && btnStopSearch != null) {
            // If there's a search let the user start or pause it
            if (configModel.getSearchExecutor().isPresent()) {
                SearchExecutor<?> search = configModel.getSearchExecutor().get();
                if (!search.isRunning()) {
                    btnSearch.setText("Start Search");
                    btnSearch.setDisable(false);
                    btnStopSearch.setDisable(true);
                } else {
                    btnSearch.setText("Pause");
                    btnSearch.setDisable(false);
                    btnStopSearch.setDisable(false);
                }
            }
            // Otherwise the search configuration needs to be changed
            else {
                btnSearch.setText("Start Search");
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

        if (!(item instanceof SearchConfigurationModel)) {
            return;
        }

        SearchConfigurationModel configModel = (SearchConfigurationModel) item;

        // Check that there's an active dataset before starting the search
        configModel.getSearchExecutor().ifPresent(search -> {
            if (search.isRunning()) {
                stopSearch(actionEvent);
            } else {
                Thread thread = new Thread(search);
                thread.start();
                searchService.searchesProperty().put(configModel.getId(), search);
            }
        });
        updateWorkspace();
    }

    /**
     * <p>Stops the provided search</p>
     *
     * @param actionEvent The action that triggered the event
     */
    public void stopSearch(ActionEvent actionEvent) {
        Displayable item = getWorkspaceService().getActiveWorkspaceItem();

        if (!(item instanceof SearchConfigurationModel)) {
            return;
        }

        SearchConfigurationModel configModel = (SearchConfigurationModel) item;
        configModel.getSearchExecutor().ifPresent(search -> {
            lcSearchProgress.getData().clear();
            lcSearchProgress.getData().add(search.getPlots());

            search.stop();
            searchService.searchesProperty().remove(configModel.getId());
        });
        updateWorkspace();

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
     * <p>Returns the workspace service of this controller</p>
     *
     * @return the workspace service of the controller
     */
    private WorkspaceService getWorkspaceService() {
        return workspaceService;
    }

    /**
     * <p>Returns the search service of this controller</p>
     *
     * @return the search service of the controller
     */
    private SearchService getSearchService() {
        return searchService;
    }
}
