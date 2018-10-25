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
import org.controlsfx.glyphfont.FontAwesome;
import org.iconic.config.IconService;
import org.iconic.control.WorkspaceTab;
import org.iconic.ea.data.DataManager;
import org.iconic.ea.data.FeatureClass;
import org.iconic.project.Displayable;
import org.iconic.project.dataset.DatasetModel;
import org.iconic.project.search.config.SearchConfigurationModel;
import org.iconic.project.search.io.SearchExecutor;
import org.iconic.project.search.io.SearchState;
import org.iconic.workspace.WorkspaceService;

import java.net.URL;
import java.util.HashMap;
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

    private final IconService iconService;
    private final WorkspaceService workspaceService;

    private InvalidationListener workspaceListener;
    private InvalidationListener consoleListener;
    private Lock updating;

    @FXML
    private WorkspaceTab searchTab;
    @FXML
    private Button btnStartSearch;
    @FXML
    private Button btnPauseSearch;
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
            final IconService iconService
    ) {
        this.iconService = iconService;
        this.workspaceService = workspaceService;
        this.updating = new ReentrantLock();

        // Update the connsole and workspace whenever the active item changes
        workspaceListener = observable -> updateWorkspace();
        consoleListener = observable -> updateConsole();
        getWorkspaceService().activeWorkspaceItemProperty().addListener(workspaceListener);
        getWorkspaceService().activeWorkspaceItemProperty().addListener(consoleListener);
    }

    @Override
    public void initialize(URL arg1, ResourceBundle arg2) {
        btnStartSearch.setGraphic(getIconService().getIcon(FontAwesome.Glyph.PLAY));
        btnPauseSearch.setGraphic(getIconService().getIcon(FontAwesome.Glyph.PAUSE));
        btnStopSearch.setGraphic(getIconService().getIcon(FontAwesome.Glyph.STOP));

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

        updateWorkspace();
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

        // If there's a search let the user start or pause it
        if (search.getSearchExecutor().isPresent()) {
            SearchExecutor<?> executor = search.getSearchExecutor().get();
            executor.getStateProperty().removeListener(workspaceListener);
            executor.getStateProperty().addListener(workspaceListener);
            updatePlots(executor);
            updateStatistics(executor);
            updateButtons(executor);
        }
    }

    /**
     * Update the search progress over time graph.
     * @param executor Executor in use
     */
    @SuppressWarnings("unchecked")
    private synchronized void updatePlots(SearchExecutor<?> executor) {
        Platform.runLater(() -> {
            ObservableList<XYChart.Series<Number, Number>> data = FXCollections.observableArrayList(executor.getPlots());
            lcSearchProgress.setData(data);
        });
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
     * Enable or disable buttons given the state
     * @param executor Executor
     */
    private void updateButtons(SearchExecutor<?> executor) {
        Platform.runLater(() -> {
            switch (executor.getState()) {
                case RUNNING:
                    getBtnStartSearch().setDisable(true);
                    getBtnPauseSearch().setDisable(false);
                    getBtnStopSearch().setDisable(false);
                    break;
                case PAUSED:
                    getBtnStartSearch().setDisable(false);
                    getBtnPauseSearch().setDisable(true);
                    getBtnStopSearch().setDisable(false);
                    break;
                case STOPPED:
                    getBtnStartSearch().setDisable(false);
                    getBtnPauseSearch().setDisable(true);
                    getBtnStopSearch().setDisable(true);
                    break;
            }
        });
    }

    /**
     * Starts a search using the currently selected search configuration.
     *
     * @param actionEvent The action that triggered this event
     */
    public void startSearch(ActionEvent actionEvent) {
        Displayable item = getWorkspaceService().getActiveWorkspaceItem();

        // Check that there's an active search configuration before starting the search
        if (!(item instanceof SearchConfigurationModel)) {
            return;
        }

        // Get the search model
        SearchConfigurationModel search = (SearchConfigurationModel) item;

        // If we are stopped, we want everything fresh
        search.getSearchExecutor().ifPresent(executor -> {
            if (executor.getState() == SearchState.STOPPED) {
                search.setChanged(true);
            }
        });

        // Make sure we've prepared
        validateConfiguration(search);

        // If there's no search already being performed on the dataset, the configuration is invalid so ignore it
        search.getSearchExecutor().ifPresent(executor -> {
            // If the search is not running start one
            switch (executor.getState()) {
                case RUNNING:
                case PAUSED:
                    executor.setState(SearchState.RUNNING);
                    break;
                case STOPPED:
                    executor.setState(SearchState.RUNNING);
                    Platform.runLater(() -> new Thread(executor).start());
                    break;
            }
        });
        updateWorkspace();
        updateConsole();
    }

    /**
     * Do the necessary checks per dataset
     * @param search Search configuration model
     */
    private void validateConfiguration(SearchConfigurationModel search) {

        // Check the search model has a defined dataset to operate on
        if (!search.getDatasetModel().isPresent()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Search Invalid");
            alert.setHeaderText("Missing Dataset");
            alert.setContentText("The Defined Search is missing a dataset to operate on! Please visit the 'Define Search' tab and select a dataset.");
            alert.showAndWait();
            return;
        }

        // Get the dataset Model - Check that the dataset doesn't have any missing values
        if (search.getDatasetModel().isPresent()) {
            DatasetModel datasetModel = search.getDatasetModel().get();
            DataManager<Double> dataManager = datasetModel.getDataManager();
            HashMap<String, FeatureClass<Number>> dataset = dataManager.getDataset();

            // Check the dataset for any missing values
            for (FeatureClass<Number> featureClass : dataset.values()) {
                if (featureClass.isMissingValues()) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION, "The dataset contains missing values! Please visit the 'Process Data' tab to remove these missing values.");
                    alert.showAndWait();
                    return;
                }
            }
        }
    }

    /**
     * Pauses a search using the currently selected search configuration.
     *
     * @param actionEvent The action that triggered this event
     */
    public void pauseSearch(ActionEvent actionEvent) {
        Displayable item = getWorkspaceService().getActiveWorkspaceItem();

        // Check that there's an active search configuration before pausing the search
        if (!(item instanceof SearchConfigurationModel)) {
            return;
        }

        SearchConfigurationModel search = (SearchConfigurationModel) item;
        // If there's no search already being performed on the dataset, the configuration is invalid
        // so ignore it
        search.getSearchExecutor().ifPresent(executor -> {
            // If the search is running stop it
            switch (executor.getState()) {
                case RUNNING:
                    executor.pause();
                    updateStatistics(executor);
                    break;
                case PAUSED:
                case STOPPED:
                    break;
            }
        });
        updateWorkspace();
        updateConsole();
    }

    /**
     * <p>Stops the provided search
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
            executor.stop();
            updateStatistics(executor);
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
        if (btnStartSearch != null) {
            btnStartSearch.setText("Start Search");
            btnStartSearch.setDisable(true);
        }
        if (btnStopSearch != null) {
            btnStopSearch.setDisable(true);
        }
    }

    // -- Getters --
    /**
     * Returns the icon service of this controller
     *
     * @return the icon service of the controller
     */
    public IconService getIconService() {
        return iconService;
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
     * Get the Button for starting a search
     * @return Button for starting a search
     */
    public Button getBtnStartSearch() {
        return btnStartSearch;
    }

    /**
     * Get the Button for pausing a search
     * @return Button for pausing a search
     */
    public Button getBtnPauseSearch() {
        return btnPauseSearch;
    }

    /**
     * Get the Button for stopping a search
     * @return Button for stopping a search
     */
    public Button getBtnStopSearch() {
        return btnStopSearch;
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
