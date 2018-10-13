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
import javafx.beans.InvalidationListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import org.iconic.project.Displayable;
import org.iconic.project.dataset.DatasetModel;
import org.iconic.project.definition.DefineSearchService;
import org.iconic.workspace.WorkspaceService;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

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

    private Lock updating;

    @FXML
    private Button btnSearch;
    @FXML
    private Button btnStopSearch;
    @FXML
    private LineChart<Number, Number> lcSearchProgress;

    @FXML
    private Text txtTime;
    @FXML
    private Text txtGen;
    @FXML
    private Text txtGenSec;
    @FXML
    private Text txtLastImprov;
    @FXML
    private Text txtAvgImprov;
    @FXML
    private Text txtCores;

    /**
     * Constructs a new StartSearchController that attaches an invalidation listener onto the search and workspace
     * services.
     */
    @Inject
    public StartSearchController(final WorkspaceService workspaceService, final SearchService searchService, final DefineSearchService defineSearchService) {
        this.defineSearchService = defineSearchService;
        this.searchService = searchService;
        this.workspaceService = workspaceService;
        this.updating = new ReentrantLock();

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
            return;
        }

        // Make sure that all the UI elements actually exist
        if (btnSearch == null || btnStopSearch == null) {
            return;
        }

        DatasetModel dataset = (DatasetModel) item;
        // Check if a search on the current active dataset is being performed
        SearchExecutor search = getSearchService().searchesProperty().get(dataset.getId());

        // If there's no search...
        if (search == null) {
            btnSearch.setText("Start Search");
            btnSearch.setDisable(false);
            btnStopSearch.setDisable(true);
        }
        // Otherwise...
        else {
            btnSearch.setText("Pause");
            btnSearch.setDisable(true);
            btnStopSearch.setDisable(false);
            updatePlots(search);
            updateStatistics(search);
        }

    }

    /**
     * Update the search progress over time graph.
     * @param search SearchModel in use
     */
    private void updatePlots(SearchExecutor search) {
        lcSearchProgress.getData().clear();
        lcSearchProgress.getData().add(search.getPlots());
    }

    /**
     * Update the statistics section
     * @param search SearchModel in use
     */
    private void updateStatistics(SearchExecutor search) {
        if (updating.tryLock()) {
            new SearchStatistics(this, search, updating).start();
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
            log.info("Function for use: " + defineSearchService.getFunction());

            DatasetModel dataset = (DatasetModel) item;
            SearchExecutor search = getSearchService().searchesProperty().get(dataset.getId());

            // If there's no search already being performed on the dataset, start a new one
            if (search == null) {
                SearchExecutor newSearch = defineSearchService.getSearchModel(dataset);
                getSearchService().searchesProperty().put(dataset.getId(), newSearch);
                Thread thread = new Thread(getSearchService().searchesProperty().get(dataset.getId()));
                thread.start();
            }
            // Otherwise stop the current search
            else {
                // TODO implement pause functionality
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
            DatasetModel dataset = (DatasetModel) item;
            SearchExecutor search = getSearchService().searchesProperty().get(dataset.getId());

            updateStatistics(search);
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

        // Disable the search buttons
        if (btnSearch != null) {
            btnSearch.setText("Start Search");
            btnSearch.setDisable(true);
        }
        if (btnStopSearch != null) {
            btnStopSearch.setDisable(true);
        }
    }

    // -- Getters --

    /**
     * Returns the workspace service of this controller
     * @return the workspace service of the controller
     */
    private WorkspaceService getWorkspaceService() {
        return workspaceService;
    }

    /**
     * Returns the search service of this controller
     * @return the search service of the controller
     */
    private SearchService getSearchService() {
        return searchService;
    }

    /**
     * Get the Text for time
     * @return Text for time
     */
    public Text getTxtTime() {
        return txtTime;
    }

    /**
     * Get the Text for current generation
     * @return Text for current generation
     */
    public Text getTxtGen() {
        return txtGen;
    }

    /**
     * Get the Text for generations per second
     * @return Text for generations per second
     */
    public Text getTxtGenSec() {
        return txtGenSec;
    }

    /**
     * Get the Text for last time of improvement
     * @return Text for last time of improvement
     */
    public Text getTxtLastImprov() {
        return txtLastImprov;
    }

    /**
     * Get the Text for average time between improvements
     * @return Text for average time between improvements
     */
    public Text getTxtAvgImprov() {
        return txtAvgImprov;
    }

    /**
     * Get the Text for cores in use
     * @return Text for cores in use
     */
    public Text getTxtCores() {
        return txtCores;
    }
}
