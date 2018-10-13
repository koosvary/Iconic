/**
 * Copyright (C) 2018 Iconic
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.iconic.project.search;

import com.google.inject.Inject;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.control.Button;
import lombok.extern.log4j.Log4j2;
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
     * Constructs a new StartSearchController that attaches an invalidation listener onto the search and workspace
     * services.
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

        // Update the workspace whenever the active item changes
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

        SearchConfigurationModel search = (SearchConfigurationModel) item;

        // Make sure that all the UI elements actually exist
        if (btnSearch != null && btnStopSearch != null) {
            // If there's a search let the user start or pause it
            if (search.getSearchExecutor().isPresent()) {
                SearchExecutor<?> executor = search.getSearchExecutor().get();

                if (!executor.isRunning()) {
                    Platform.runLater(() -> {
                                btnSearch.setText("Start Search");
                                btnSearch.setDisable(false);
                                btnStopSearch.setDisable(true);
                            }
                    );
                } else {
                    Platform.runLater(() -> {
                                btnSearch.setText("Pause");
                                btnSearch.setDisable(false);
                                btnStopSearch.setDisable(false);
                            }
                    );
                }
            }
            // Otherwise the search configuration needs to be changed
            else {
                Platform.runLater(() -> {
                            btnSearch.setText("Start Search");
                            btnSearch.setDisable(true);
                            btnStopSearch.setDisable(true);
                        }
                );
            }
        }
        // Otherwise...
        else {
            Platform.runLater(() -> {
                        btnSearch.setText("Pause");
                        btnSearch.setDisable(true);
                        btnStopSearch.setDisable(false);
                    }
            );
            updatePlots(search);
        }
    }

    private synchronized void updatePlots(final SearchConfigurationModel search) {
        search.getSearchExecutor().ifPresent(executor -> {
            Platform.runLater(() -> {
                lcSearchProgress.getData().clear();
                lcSearchProgress.getData().add(executor.getPlots());
            });
        });
    }

    /**
     * Starts a search using the currently selected dataset.
     *
     * @param actionEvent The action that triggered this event
     */
    public void startSearch(ActionEvent actionEvent) {
        Displayable item = getWorkspaceService().getActiveWorkspaceItem();

        // Check that there's an active search configuration before starting the search
        if (!(item instanceof SearchConfigurationModel)) {
            return;
        }

        SearchConfigurationModel search = (SearchConfigurationModel) item;
        // If there's no search already being performed on the dataset, the configuration is invalid
        // so ignore it
        search.getSearchExecutor().ifPresent(executor -> {
            // If the search is running stop it
            if (executor.isRunning()) {
                stopSearch(actionEvent);
            }
            // Otherwise start it
            else {
                executor.setRunning(true);
                Platform.runLater(() -> {
                    getSearchService().searchesProperty().put(search.getId(), executor);
                    Thread thread = new Thread(getSearchService().searchesProperty().get(search.getId()));
                    thread.start();
                });
            }
        });
    }

    /**
     * <p>Stops the provided search</p>
     *
     * @param actionEvent The action that triggered the event
     */
    public void stopSearch(ActionEvent actionEvent) {
        Displayable item = getWorkspaceService().getActiveWorkspaceItem();
        // Check that there's an active search configuration before starting the search
        if (!(item instanceof SearchConfigurationModel)) {
            return;
        }

        SearchConfigurationModel search = (SearchConfigurationModel) item;

        search.getSearchExecutor().ifPresent(executor -> {
            lcSearchProgress.getData().clear();
            lcSearchProgress.getData().add(executor.getPlots());
            executor.stop();
            searchService.searchesProperty().remove(search.getId());
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

        // Disable the search buttons
        if (btnSearch != null) {
            btnSearch.setText("Start Search");
            btnSearch.setDisable(true);
        }
        if (btnStopSearch != null) {
            btnStopSearch.setDisable(true);
        }
    }

    /**
     * Returns the workspace service of this controller
     *
     * @return the workspace service of the controller
     */
    private WorkspaceService getWorkspaceService() {
        return workspaceService;
    }

    /**
     * Returns the search service of this controller
     *
     * @return the search service of the controller
     */
    private SearchService getSearchService() {
        return searchService;
    }
}
