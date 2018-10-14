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
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.AnchorPane;
import lombok.extern.log4j.Log4j2;
import org.iconic.control.WorkspaceTab;
import org.iconic.project.Displayable;
import org.iconic.project.search.config.SearchConfigurationModel;
import org.iconic.project.search.io.SearchExecutor;
import org.iconic.views.ViewService;
import org.iconic.workspace.WorkspaceService;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

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

    private Lock updating;

    @FXML
    private WorkspaceTab searchTab;
    @FXML
    private Button btnSearch;
    @FXML
    private Button btnStopSearch;
    @FXML
    private LineChart<Number, Number> lcSearchProgress;
    @FXML
    private Label txtTime;
    @FXML
    private Label txtGen;
    @FXML
    private Label txtGenSec;
    @FXML
    private Label txtLastImprov;
    @FXML
    private Label txtAvgImprov;
    @FXML
    private Label txtCores;
    @FXML
    private AnchorPane consoleArea;
    @FXML
    private ListView<String> consoleContent;

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
        this.updating = new ReentrantLock();

        // Update the connsole and workspace whenever the active item changes
        InvalidationListener selectionChangedWorkspaceListener = observable -> updateWorkspace();
        InvalidationListener selectionChangedConsoleListener = observable -> updateConsole();
        getWorkspaceService().activeWorkspaceItemProperty().addListener(selectionChangedWorkspaceListener);
        getWorkspaceService().activeWorkspaceItemProperty().addListener(selectionChangedConsoleListener);
    }

    @Override
    public void initialize(URL arg1, ResourceBundle arg2) {
        updateWorkspace();

        consoleContent.prefHeightProperty().bind(consoleArea.prefHeightProperty());
        consoleContent.prefWidthProperty().bind(consoleArea.prefWidthProperty());
        consoleContent.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        MenuItem miCopy = new MenuItem("Copy");
        miCopy.setOnAction(this::copyAction);
        miCopy.setAccelerator(KeyCombination.keyCombination("Shortcut+C"));

        ContextMenu menu = new ContextMenu();
        menu.getItems().add(miCopy);
        consoleContent.setContextMenu(menu);
        updateConsole();

        searchTab.setOnSelectionChanged(event -> {
            updateWorkspace();
            updateConsole();
        });
    }
    /**
     * Updates the workspace to match the current active dataset.
     */
    private synchronized void updateConsole() {
        Displayable item = getWorkspaceService().getActiveWorkspaceItem();

        Platform.runLater(() -> {
            consoleContent.itemsProperty().unbind();
            consoleContent.setItems(FXCollections.emptyObservableList());
        });

        // Check the console tab pane as there's no guarantee it will exist when this is triggered
        if (!(item instanceof SearchConfigurationModel)) {
            return;
        }

        SearchConfigurationModel search = (SearchConfigurationModel) item;

        search.getSearchExecutor().ifPresent(executor -> {
            Platform.runLater(() -> {
                consoleContent.itemsProperty().bind(executor.updatesProperty());
            });
        });
    }
    /**
     * Updates the workspace to match the current active dataset.
     */
    private synchronized void updateWorkspace() {
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
                updatePlots(executor);
                updateStatistics(executor);

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
        }
    }

    /**
     * Update the search progress over time graph.
     * @param executor Executor in use
     */
    @SuppressWarnings("unchecked")
    private synchronized void updatePlots(SearchExecutor<?> executor) {
        ObservableList<XYChart.Series<Number, Number>> data = FXCollections.observableArrayList(executor.getPlots());
        lcSearchProgress.setData(data);
    }

    /**
     * Update the statistics section
     * @param executor Executor in use
     */
    private void updateStatistics(SearchExecutor<?> executor) {
        if (updating.tryLock()) {
            new SearchStatistics(workspaceService, this, executor, updating).start();
        }
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
                    Thread thread = new Thread(executor);
                    thread.start();
                });
            }
        });
        updateWorkspace();
        updateConsole();
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
            updateStatistics(executor);
            executor.stop();
        });
        updateWorkspace();
        updateConsole();
    }

    private void copyAction(ActionEvent actionEvent) {
        final ClipboardContent clipboard = new ClipboardContent();
        final StringBuilder out = new StringBuilder();

        consoleContent.getSelectionModel().getSelectedItems().stream().filter(Objects::nonNull)
                .forEach(item -> out.append(item).append("\n"));

        clipboard.putString(out.toString());
        Clipboard.getSystemClipboard().setContent(clipboard);
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

    /**
     * Get the Text for time
     * @return Text for time
     */
    public Label getTxtTime() {
        return txtTime;
    }

    /**
     * Get the Text for current generation
     * @return Text for current generation
     */
    public Label getTxtGen() {
        return txtGen;
    }

    /**
     * Get the Text for generations per second
     * @return Text for generations per second
     */
    public Label getTxtGenSec() {
        return txtGenSec;
    }

    /**
     * Get the Text for last time of improvement
     * @return Text for last time of improvement
     */
    public Label getTxtLastImprov() {
        return txtLastImprov;
    }

    /**
     * Get the Text for average time between improvements
     * @return Text for average time between improvements
     */
    public Label getTxtAvgImprov() {
        return txtAvgImprov;
    }

    /**
     * Get the Text for cores in use
     * @return Text for cores in use
     */
    public Label getTxtCores() {
        return txtCores;
    }
}
