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
        package org.iconic.project.results;

import com.google.inject.Inject;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.converter.DoubleStringConverter;
import javafx.util.converter.IntegerStringConverter;
import lombok.extern.log4j.Log4j2;
import org.iconic.control.WorkspaceTab;
import org.iconic.ea.chromosome.Chromosome;
import org.iconic.ea.data.DataManager;
import org.iconic.ea.data.FeatureClass;
import org.iconic.project.Displayable;
import org.iconic.project.search.SolutionStorage;
import org.iconic.project.search.config.SearchConfigurationModel;
import org.iconic.workspace.WorkspaceService;

import java.net.URL;
import java.util.*;

/**
 * A controller for the Results view
 */
@Log4j2
public class ResultsController implements Initializable {

    private final WorkspaceService workspaceService;

    private SolutionStorage<Chromosome<?>> storage;
    private SearchConfigurationModel lastSearch;
    private InvalidationListener resultAddedListener;

    @FXML
    private WorkspaceTab resultsTab;
    @FXML
    private TableView<ResultDisplay> solutionsTableView;
    @FXML
    private LineChart<Number,Number> solutionsPlot;


    /**
     * Constructs a new ResultsController that attaches an invalidation listener onto the workspace service.
     */
    @Inject
    public ResultsController(final WorkspaceService workspaceService) {
        this.workspaceService = workspaceService;

        // Update the workspace whenever the active dataset changes
        resultAddedListener = observable -> updateWorkspace();
        getWorkspaceService().activeWorkspaceItemProperty().addListener(resultAddedListener);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void initialize(URL arg1, ResourceBundle arg2) {
        updateWorkspace();

        resultsTab.setOnSelectionChanged(event -> updateWorkspace());

        // Listener for the solutions being clicked in the table
        solutionsTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                graphExpectedValues();
            }
        });
    }

    /**
     * Calls the main thread to update the workspace when it can.
     */
    private synchronized void updateWorkspace() {
        Displayable item = getWorkspaceService().getActiveWorkspaceItem();

        // If no dataset, stop what you're doing.
        if (!(item instanceof SearchConfigurationModel)) {
            // TODO clear the UI?
            return;
        }

        SearchConfigurationModel search = (SearchConfigurationModel) item;
        search.getSearchExecutor().ifPresent(executor -> {
            if (search != lastSearch) {
                // If a search is running, use that current one for results. Else use the last search
                //noinspection unchecked
                storage = (SolutionStorage<Chromosome<?>>) executor.getSolutionStorage();
                storage.getSolutions().addListener(resultAddedListener);
                lastSearch = search;
            }

        });
        if (storage == null) {
            // No storage? No worries
            return;
        }

        Platform.runLater(this::updateWorkspaceMainThread);
    }

    public void graphExpectedValues() {
        System.out.println("ResultsController   graphExpectedValues");
        Displayable item = getWorkspaceService().getActiveWorkspaceItem();

        // If no dataset, stop what you're doing.
        if (!(item instanceof SearchConfigurationModel)) {
            // TODO clear the UI?
            return;
        }

        SearchConfigurationModel search = (SearchConfigurationModel) item;

        // Dataset model is not present
        if (!search.getDatasetModel().isPresent()) {
            return;
        }

        // Get the dataset
        DataManager dataManager = search.getDatasetModel().get().getDataManager();
        HashMap<String, FeatureClass<Number>> dataset = dataManager.getDataset();

        // Get the first output Feature we find
        FeatureClass<Number> outputFeatureClass = null;
        for (FeatureClass<Number> featureClass : dataset.values()) {
            if (featureClass.isOutput()) {
                outputFeatureClass = featureClass;
                break;
            }
        }

        // No output feature class found.
        if (outputFeatureClass == null) {
            log.info("No output feature class was found in the dataset to be used to graph expected results vs actual results");
            return;
        }


        // Create the two new lines
        XYChart.Series series1 = new XYChart.Series();
        XYChart.Series series2 = new XYChart.Series();

        // Set the labels for the legend
        series1.setName("Expected Values");
        series2.setName("Actual Values");

        // Get all the expected values
        List<Number> samples = outputFeatureClass.getSamples();
        for (int i = 0; i < samples.size(); i++) {
            series1.getData().add(new XYChart.Data(i, samples.get(i)));
        }

        // TODO - Get the actual values
        //series2.getData().add(new XYChart.Data(i, samples.get(i)));


        solutionsPlot.setAnimated(false);
        solutionsPlot.setCreateSymbols(true);

        solutionsPlot.getData().clear();
        solutionsPlot.getData().addAll(series1, series2);
    }

    /**
     * Updates the workspace to match the current active dataset.
     */
    private synchronized void updateWorkspaceMainThread() {
        List<ResultDisplay> resultDisplays = new ArrayList<>();
        for (Map.Entry<Integer, List<Chromosome<?>>> entry : storage.getSolutions().entrySet()) {
            Chromosome<?> result = entry.getValue().get(0);
            resultDisplays.add(new ResultDisplay(result.getSize(), result.getFitness(), result.toString()));
            resultDisplays.add(new ResultDisplay(result.getSize(), result.getFitness(), result.simplifyExpression(
                    result.getExpression(result.toString(), new ArrayList<>(lastSearch.getEnabledPrimitives()), true)
            )));
        }

        // Add all the results as FX observables
        solutionsTableView.setItems(FXCollections.observableArrayList(resultDisplays));

        TableColumn<ResultDisplay, Integer> sizeCol = new TableColumn<>("Size");
        TableColumn<ResultDisplay, Double> fitnessCol = new TableColumn<>("Fitness");
        TableColumn<ResultDisplay, String> solutionCol = new TableColumn<>("Solution");

        // Set conversion factories for data types into string
        sizeCol.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        fitnessCol.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));

        // Set where the values come from
        sizeCol.setCellValueFactory(cellData -> cellData.getValue().sizeProperty().asObject());
        fitnessCol.setCellValueFactory(cellData -> cellData.getValue().fitProperty().asObject());
        solutionCol.setCellValueFactory(cellData -> cellData.getValue().solutionProperty());

        // Set the columns to be these ones
        solutionsTableView.getColumns().setAll(sizeCol, fitnessCol, solutionCol);
    }

    /**
     * Get the workspace service
     * @return Workspace service
     */
    private WorkspaceService getWorkspaceService() {
        return workspaceService;
    }

    public void solutionPlotTest() {
        System.out.println("ResultsController   solutionPlotTest");

        XYChart.Series series1 = new XYChart.Series();
        series1.setName("Equities");
        series1.getData().add(new XYChart.Data(4.2, 193.2));
        series1.getData().add(new XYChart.Data(2.8, 33.6));
        series1.getData().add(new XYChart.Data(6.8, 23.6));

        XYChart.Series series2 = new XYChart.Series();
        series2.setName("Mutual funds");
        series2.getData().add(new XYChart.Data(5.2, 229.2));
        series2.getData().add(new XYChart.Data(2.4, 37.6));
        series2.getData().add(new XYChart.Data(6.4, 15.6));

        solutionsPlot.setAnimated(false);
        solutionsPlot.setCreateSymbols(true);

        solutionsPlot.getData().addAll(series1, series2);
    }
}
