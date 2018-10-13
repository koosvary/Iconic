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
package org.iconic.project;

import javafx.beans.InvalidationListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import org.controlsfx.control.spreadsheet.*;
import org.iconic.ea.data.DataManager;
import org.iconic.project.dataset.DatasetModel;
import org.iconic.workspace.WorkspaceService;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class InputDataController implements Initializable {
    private final ProjectService projectService;
    private final WorkspaceService workspaceService;

    private ObservableList<String> rowHeaders = FXCollections.observableArrayList();
    private String infoPlaceholder = "Enter variable description here.";

    @FXML
    private Spreadsheet spreadsheet;
    @FXML
    private Button btnCreateDataset;
    @FXML
    private Button btnImportDataset;
    @FXML
    private Button btnExportDataset;
    @FXML
    private HBox createButtonHBox;
    @FXML
    private HBox importButtonHBox;
    @FXML
    private Text welcomeMessage;

    @Inject
    public InputDataController(final ProjectService projectService, final WorkspaceService workspaceService) {
        this.projectService = projectService;
        this.workspaceService = workspaceService;

        // Update the workspace whenever the active dataset changes
        InvalidationListener selectionChangedListener = observable -> updateWorkspace();
        getWorkspaceService().activeWorkspaceItemProperty().addListener(selectionChangedListener);
    }

    /**
     * Changes what is shown to the user according to the active workspace item. For example shows the current
     * dataset if that is the active item.
     */
    private void updateWorkspace() {
        Displayable item = getWorkspaceService().getActiveWorkspaceItem();

        // If no dataset is selected clear the UI
        if (!(item instanceof DatasetModel)) {
            clearUI();
        }
        else{
            fillSpreadsheetByRow();
            spreadsheet.setVisible(true);
            createButtonHBox.setVisible(false);
            importButtonHBox.setVisible(false);
            welcomeMessage.setText("Save this dataset to a file.");
        }
    }

    /**
     * Hides the spreadsheet and export spreadsheet button to show what would be classified as the home page.
     */
    private void clearUI() {
        spreadsheet.setGrid(new GridBase(0,0));
        spreadsheet.setVisible(false);
        rowHeaders.clear();
        createButtonHBox.setVisible(true);
        importButtonHBox.setVisible(true);
        welcomeMessage.setText("Welcome, create or import a dataset to get started.");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //Binds the visibility of the spreadsheet to the export dataset button
        spreadsheet.managedProperty().bind(spreadsheet.visibleProperty());
        btnExportDataset.managedProperty().bind(btnExportDataset.visibleProperty());
        btnExportDataset.visibleProperty().bind(spreadsheet.visibleProperty());

        updateWorkspace();
    }

    /**
     * Adds empty buffer rows to a small or empty dataset. The number of rows added is calculated according to the
     * height of the spreadsheetview.
     *
     * This method requires prefHeight of spreadsheet to be set
     */
    private void addToEmptySpreadsheetView(){
        double spreadsheetHeight = spreadsheet.getPrefHeight();
        double cellHeight = spreadsheet.getRowHeight(1);
        for(int i = 0; i < spreadsheetHeight; i += cellHeight){
            spreadsheetAddRow();
        }
        for(int i = 0; i < 26; i ++){
            spreadsheetAddColumn();
        }
    }

    /**
     * Reads the current dataset selected within the project and fills the spreadsheet row by row
     */
    private void fillSpreadsheetByRow(){
        //Retrieves the dataset and its size
        Optional<DataManager<Double>> dataManager = getDataManager();
        int datasetRowCount = dataManager.get().getSampleSize();
        int datasetColumnCount = dataManager.get().getFeatureSize();

        //A grid is set up using the width of the current dataset
        GridBase grid;
        if(dataManager.get().containsHeader()){
            grid = new GridBase(datasetRowCount+2, datasetColumnCount);
        }
        else{
            grid = new GridBase(datasetRowCount+1, datasetColumnCount);
        }

        //The rows variable stores the values of the dataset as spreadsheet cells
        ObservableList<ObservableList<SpreadsheetCell>> rows = FXCollections.observableArrayList();
        //This list contains the row headers so that the number starts after the info and header rows
        rowHeaders = FXCollections.observableArrayList();
        //Keeps track of the current row of the spreadsheet
        int spreadsheetRow = 0;

        //Creates a row above the data for adding a column description
        ObservableList<SpreadsheetCell> infoList = FXCollections.observableArrayList();
        for (int column = 0; column < datasetColumnCount; ++column) {
            String cellContents = infoPlaceholder;
            SpreadsheetCell nextCell = SpreadsheetCellType.STRING.createCell(spreadsheetRow, column, 1, 1, cellContents);
            int finalColumn = column;
            //If the description is changed update it in the dataset
            nextCell.itemProperty().addListener((observable, oldValue, newValue) -> {
                try{
                    String newHeader = String.valueOf(newValue);
                    updateVariableDescriptions(finalColumn,newHeader);
                }catch (Exception e) {
                    nextCell.setItem(oldValue);
                }
            });
            infoList.add(nextCell);
        }
        rows.add(infoList);
        rowHeaders.add("info");
        spreadsheetRow++;

        //If the datset has headers they are added to the spreadsheet before the data
        if(dataManager.get().containsHeader()){
            final ObservableList<SpreadsheetCell> list = FXCollections.observableArrayList();
            for (int column = 0; column < datasetColumnCount; ++column) {
                String cellContents = String.valueOf(dataManager.get().getSampleHeaders().get(column));
                SpreadsheetCell nextCell = SpreadsheetCellType.STRING.createCell(spreadsheetRow, column, 1, 1, cellContents);
                int finalColumn = column;
                //If the header is changed update it in the dataset
                nextCell.itemProperty().addListener((observable, oldValue, newValue) -> {
                    try{
                        String newHeader = String.valueOf(newValue);
                        updateProjectHeaders(finalColumn,newHeader);
                    }catch (Exception e) {
                        nextCell.setItem(oldValue);
                    }
                });
                list.add(nextCell);
            }
            rows.add(list);
            rowHeaders.add("name");
            spreadsheetRow++;
        }

        //For each row in the dataset add it to the spreadsheet below the info and header rows
        for(int datasetRow = 0; datasetRow < datasetRowCount; datasetRow++){
            final ObservableList<SpreadsheetCell> list = FXCollections.observableArrayList();
            for (int column = 0; column < grid.getColumnCount(); ++column) {
                String cellContents = String.valueOf(dataManager.get().getSampleRow(datasetRow).get(column));
                //Adds the cell at spreadsheetRow (below the last row added)
                SpreadsheetCell nextCell = SpreadsheetCellType.STRING.createCell(spreadsheetRow, column, 1, 1, cellContents);
                //If the value is changed in the spreadsheet the dataset is updated
                addChangeListenerToCell(nextCell, datasetRow, column);
                list.add(nextCell);
            }
            rows.add(list);
            rowHeaders.add(String.valueOf(datasetRow+1));
            spreadsheetRow++;
        }
        //Adds the spreadsheet cells to the grid
        grid.setRows(rows);
        grid.getRowHeaders().setAll(rowHeaders);
        //Add the grid to the spreadsheet view
        spreadsheet.setGrid(grid);
        spreadsheet.setRowHeaderWidth(50);
        //Add buffer rows in case of small or empty dataset
        addToEmptySpreadsheetView();
        //Get the Vertical and Horizontal Scroll bars and add new rows and columns when they reach their respective ends
        ScrollBar verticalScrollbar = getVerticalScrollbar(spreadsheet);
        verticalScrollbar.valueProperty().addListener(this::verticalScrolled);
        ScrollBar horizontalScrollbar = getHorizontalScrollBar(spreadsheet);
        horizontalScrollbar.valueProperty().addListener(this::horizontalScrolled);
    }

    /**
     * Returns the Vertical Scroll Bar for the given Spreadsheetview
     */
    private ScrollBar getVerticalScrollbar(SpreadsheetView table) {
        ScrollBar result = null;
        for (Node n : table.lookupAll(".scroll-bar")) {
            if (n instanceof ScrollBar) {
                ScrollBar bar = (ScrollBar) n;
                if (bar.getOrientation().equals(Orientation.VERTICAL)) {
                    result = bar;
                }
            }
        }
        return result;
    }

    /**
     * Returns the Horizontal Scroll Bar for the given Spreadsheetview
     */
    private ScrollBar getHorizontalScrollBar(SpreadsheetView table) {
        ScrollBar result = null;
        for (Node n : table.lookupAll(".scroll-bar")) {
            if (n instanceof ScrollBar) {
                ScrollBar bar = (ScrollBar) n;
                if (bar.getOrientation().equals(Orientation.HORIZONTAL)) {
                    result = bar;
                }
            }
        }
        return result;
    }

    /**
     *     Adds a new row to the spreadsheet when the vertical scroll bar is at the bottom of the table
     *     Once a new row is added the scroll bar is moved up slightly to avoid an infinite loop
     */
    private void verticalScrolled(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
        double value = newValue.doubleValue();
        ScrollBar bar = getVerticalScrollbar(spreadsheet);
        //If the scroll bar is at the bottom add a new row
        if (value == bar.getMax() && value != bar.getMin()) {
            double targetValue = value * spreadsheet.getGrid().getRows().size();
            spreadsheetAddRow();
            bar.setValue(targetValue / spreadsheet.getGrid().getRows().size());
        }
    }

    /**
     *     Adds a new column to the spreadsheet when the horizontal scroll bar is at the bottom of the table
     *     Once a new column is added the scroll bar is moved left slightly to avoid an infinite loop
     */
    private void horizontalScrolled(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
        double value = newValue.doubleValue();
        ScrollBar bar = getHorizontalScrollBar(spreadsheet);
        //If the scroll bar is at the bottom add a new row
        if (value == bar.getMax() && value != bar.getMin()) {
            double targetValue = value * spreadsheet.getGrid().getColumnCount();
            spreadsheetAddColumn();
            bar.setValue(targetValue / spreadsheet.getGrid().getColumnCount());
        }
    }

    /**
     * Adds a new row to a given SpreadsheetView's Grid on runtime, preserving all it's cells values.
     */
    private void spreadsheetAddRow() {
        Grid oldGrid = spreadsheet.getGrid();

        //The row index where the new row will be added to the spreadsheet
        int newRowPos = oldGrid.getRowCount();
        ObservableList<ObservableList<SpreadsheetCell>> rows = oldGrid.getRows();

        final ObservableList<SpreadsheetCell> list = FXCollections.observableArrayList();
        //Adds an empty cell to each new row
        for (int column = 0; column < oldGrid.getColumnCount(); ++column) {
            String cellContents = "";
            SpreadsheetCell nextCell = SpreadsheetCellType.STRING.createCell(newRowPos, column, 1, 1, cellContents);
            //Set unused cell to be grey
            nextCell.setStyle("-fx-background-color: #dcdcdc;");
            int finalColumn = column;
            //When a value is added to the new cell add the rows up to and including the row of this cell
            nextCell.itemProperty().addListener((observable, oldValue, newValue) -> {
                try{
                    //Check that entered value is a number
                    Double.parseDouble(String.valueOf(newValue));
                    addNewRowsFromSpreadsheet(newRowPos, finalColumn);
                }catch (Exception e) {
                    if(!newValue.equals("")) {
                        nextCell.setItem(oldValue);
                    }
                }
            });
            list.add(nextCell);
        }
        // Adds the new row to the rows set
        rows.add(list);
        //Minus 1 to account for info and name row
        rowHeaders.add(String.valueOf(newRowPos-1));
        // Updates the Grid rows
        oldGrid.getRowHeaders().setAll(rowHeaders);
        spreadsheet.setGrid(oldGrid);
    }

    /**
     * Adds a new column to a given SpreadsheetView's Grid on runtime, preserving all it's cells values.
     */
    private void spreadsheetAddColumn() {
        Grid oldGrid = spreadsheet.getGrid();
        //The column index where the new column will be added
        int newColumnPos = oldGrid.getColumnCount();
        ObservableList<ObservableList<SpreadsheetCell>> rows = oldGrid.getRows();

        //Create and adds the new column description cell with an appropriate change listener
        SpreadsheetCell infoCell = SpreadsheetCellType.STRING.createCell(0, newColumnPos, 1, 1, infoPlaceholder);
        infoCell.itemProperty().addListener((observable, oldValue, newValue) -> {
            try{
                String newHeader = String.valueOf(newValue);
                addNewColumnsFromSpreadsheet(0,newColumnPos);
                updateVariableDescriptions(newColumnPos,newHeader);
            }catch (Exception e) {
                infoCell.setItem(oldValue);
            }
        });
        rows.get(0).add(infoCell);

        //Create and adds the new column name cell with an appropriate change listener
        SpreadsheetCell headerCell = SpreadsheetCellType.STRING.createCell(1, newColumnPos, 1, 1, getDataManager().get().intToHeader(newColumnPos));
        headerCell.itemProperty().addListener((observable, oldValue, newValue) -> {
            try{
                String newHeader = String.valueOf(newValue);
                addNewColumnsFromSpreadsheet(1,newColumnPos);
                updateProjectHeaders(newColumnPos,newHeader);
            }catch (Exception e) {
                headerCell.setItem(oldValue);
            }
        });
        rows.get(1).add(headerCell);

        //Adds an empty cell to each new row, this loop starts at 2 to account for the previous two cells already being added
        for (int row = 2; row < rows.size(); ++row) {
            String cellContents = "";
            SpreadsheetCell nextCell = SpreadsheetCellType.STRING.createCell(row, newColumnPos, 1, 1, cellContents);
            //Set unused cell to be grey
            nextCell.setStyle("-fx-background-color: #dcdcdc;");
            int finalRow = row;
            //When a value is added to this new cell, add the columns up to and including the column of this cell
            nextCell.itemProperty().addListener((observable, oldValue, newValue) -> {
                try{
                    //Check that entered value is a number
                    Double.parseDouble(String.valueOf(newValue));
                    addNewColumnsFromSpreadsheet(finalRow, newColumnPos);
                }catch (Exception e) {
                    if(!newValue.equals("")) {
                        nextCell.setItem(oldValue);
                    }
                }
            });
            rows.get(row).add(nextCell);
        }
        //Create a new grid a column wider than the current grid
        //This needs to be done as there is no method to add a new column to a grid base
        Grid newGrid = new GridBase(rows.size(),newColumnPos+1);
        newGrid.setRows(rows);
        newGrid.getRowHeaders().setAll(rowHeaders);
        //Change the existing grid to the new wider grid
        spreadsheet.setGrid(newGrid);
        spreadsheet.setRowHeaderWidth(50);
    }

    /**
     * Adds the new rows of the spreadsheet from the end of the current dataset to the row given as
     * a parameter. Any blank cells are changed to the value 0.0
     */
    private void addNewRowsFromSpreadsheet(int finalNewRowPos, int newCellCol){
        Optional<DataManager<Double>> dataManager = getDataManager();
        int currentNewRowPos = dataManager.get().getSampleSize();
        //Allowing for info row
        currentNewRowPos++;
        if(dataManager.get().containsHeader()){
            currentNewRowPos++;
        }
        int datasetFeatureSize = dataManager.get().getFeatureSize();

        while(currentNewRowPos <= finalNewRowPos){
            List<Number> newDataValues = new ArrayList<>();
            for (int column = 0; column < datasetFeatureSize; ++column) {
                createNewCell(newDataValues, currentNewRowPos, column);
            }
            addRowToDataset(newDataValues);
            currentNewRowPos++;
        }
        if(newCellCol > datasetFeatureSize){
            addNewColumnsFromSpreadsheet(finalNewRowPos, newCellCol);
        }
    }

    /**
     * Adds the new column of the spreadsheet from the end of the current dataset to the column given as
     * a parameter. Any blank cells are changed to the value 0.0
     */
    private void addNewColumnsFromSpreadsheet(int newCellRow, int finalNewColPos){
        Optional<DataManager<Double>> dataManager = getDataManager();
        int currentNewColPos = dataManager.get().getFeatureSize();
        //Plus 2 accounting for info and header row
        int currentSpreadSheetRowCount = dataManager.get().getSampleSize() + 2;

        //For each new column
        while(currentNewColPos <= finalNewColPos){
            List<Number> newDataValues = new ArrayList<>();
            for (int currentRow = 2; currentRow < currentSpreadSheetRowCount; ++currentRow) {
                createNewCell(newDataValues, currentRow, currentNewColPos);
            }
            addColumnToDataset(spreadsheet.getGrid().getRows().get(1).get(currentNewColPos).getText(),newDataValues);
            currentNewColPos++;
        }
        if(newCellRow > currentSpreadSheetRowCount){
            addNewRowsFromSpreadsheet(newCellRow, finalNewColPos);
        }
    }

    /**
     * Given a list a new cells (row or column) each oldCell which was not included in the dataset
     * is changed to hold the value 0.0 before being added to the dataset. Additionally, the colour
     * is changed to white and a change listener is added to ensure the dataset is updated when this
     * cells contents are changed.
     */
    private void createNewCell(List<Number> newDataValues, int row, int column){
        SpreadsheetCell oldCell = spreadsheet.getGrid().getRows().get(row).get(column);
        //Set used cell to be white
        oldCell.setStyle("-fx-background-color: #ffffff;");
        String cellContents = String.valueOf(oldCell.getItem());
        if(cellContents.isEmpty()){
            cellContents = "0.0";
        }
        SpreadsheetCell newCell = SpreadsheetCellType.STRING.createCell(row, column, 1, 1, cellContents);
        //Minus two accounting for header and description
        int changedRow = row - 2;
        addChangeListenerToCell(newCell,changedRow, column);
        spreadsheet.getGrid().getRows().get(row).set(column,newCell);
        newDataValues.add(Double.parseDouble(cellContents));
    }

    /**
     * Adds a change listener to the given cell that will ensure the dataset is
     * updated when the cell is changed.
     */
    private void addChangeListenerToCell(SpreadsheetCell spreadsheetCell, int row, int column){
        spreadsheetCell.itemProperty().addListener((observable, oldValue, newValue) -> {
            try{
                Number newNumber = Double.parseDouble(String.valueOf(newValue));
                updateProjectDataset(row,column,newNumber);
            }catch (Exception e) {
                spreadsheetCell.setItem(oldValue);
            }
        });
    }

    private void addRowToDataset(List<Number> newNumbers){
        Optional<DataManager<Double>> dataManager = getDataManager();
        dataManager.get().addRow(newNumbers);
    }

    private void addColumnToDataset(String header, List<Number> newNumbers){
        Optional<DataManager<Double>> dataManager = getDataManager();
        dataManager.get().addNewFeature(header, newNumbers);
    }

    private void updateProjectDataset(int row, int column, Number newValue){
        Optional<DataManager<Double>> dataManager = getDataManager();
        dataManager.get().getSampleColumn(column).set(row,newValue);
    }

    private void updateProjectHeaders(int column, String newValue){
        Optional<DataManager<Double>> dataManager = getDataManager();
        dataManager.get().updateHeaderAtIndex(column,newValue);
    }

    private void updateVariableDescriptions(int column, String newValue){
        Optional<DataManager<Double>> dataManager = getDataManager();
        //TODO @JackR Implement column descriptions stored within datamanager as below
        //dataManager.get().updateDescriptionAtIndex(column,newValue);
    }

    /**
     *Returns the current datamanager if present
     */
    private Optional<DataManager<Double>> getDataManager() {
        Displayable item = getWorkspaceService().getActiveWorkspaceItem();

        if (item instanceof DatasetModel) {
            DatasetModel dataset = (DatasetModel) item;
            return Optional.of(dataset.getDataManager());
        } else {
            return Optional.empty();
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
     * Returns the project service of this controller
     * </p>
     *
     * @return the project service of the controller
     */
    private ProjectService getProjectService() {
        return projectService;
    }

    public void createDataset(ActionEvent actionEvent) throws IOException {
        TextInputDialog dialog = new TextInputDialog("Dataset1");
        dialog.setTitle("Create a Dataset");
        dialog.setHeaderText("You are about to create a new dataset.");
        dialog.setContentText("Please enter a dataset name:");
        // Create the project only if a name was provided
        dialog.showAndWait().ifPresent(
                name -> {
                    DatasetModel dataset = new DatasetModel(name);
                    Displayable currentItem = getWorkspaceService().getActiveWorkspaceItem();
                    setupNewDataset(currentItem, dataset);
                }
        );
    }

    /**
     * Takes a dataset and adds it to a project. If the current displayable item is
     * a project the new dataset is added to this project. Otherwise a new project
     * is created and the dataset is added to this new project.
     *
     */
    private void setupNewDataset(Displayable currentItem, DatasetModel dataset){
        // If the current active item isn't a project create a new project
        if ((!(currentItem instanceof ProjectModel)) && (!(currentItem instanceof DatasetModel))){
            TextInputDialog dialog = new TextInputDialog("Project1");
            dialog.setTitle("Create a Project");
            dialog.setHeaderText("To load a dataset, you need to create a project.");
            dialog.setContentText("Please enter a project name:");
            // Create the project only if a name was provided
            dialog.showAndWait().ifPresent(
                    name -> {
                        final ProjectModel project = ProjectModel.builder().name(name).build();
                        getWorkspaceService().setActiveWorkspaceItem(project);
                        getProjectService().getProjects().add(project);
                    }
            );
        }
        //This if statement is required to check if user clicked cancel in previous dialog
        if (getWorkspaceService().getActiveWorkspaceItem() instanceof ProjectModel) {
            ProjectModel project = (ProjectModel) getWorkspaceService().getActiveWorkspaceItem();
            ProjectModel newProject = project.toBuilder().dataset(dataset).build();
            getWorkspaceService().setActiveWorkspaceItem(null);
            //Keeps the project list in the same order
            getProjectService().getProjects().set(getProjectService().getProjects().indexOf(project),newProject);
            getWorkspaceService().setActiveWorkspaceItem(newProject);
        }
    }

    /**
     * Opens a file dialog to save the current dataset as a text file or csv
     *
     * @param actionEvent
     * @throws IOException
     */
    public void saveDataset(ActionEvent actionEvent) throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Dataset");
        // Show only .txt and .csv files
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Text files", "*.txt", "*.csv")
        );

        // Show the file dialog over the parent window
        File f = fileChooser.showSaveDialog(spreadsheet.getScene().getWindow());

        if(f != null && getDataManager().isPresent()){
            getDataManager().get().saveDatasetToFile(f);
        }
    }

    /**
     * <p>
     * Opens a file dialog for choosing a dataset to import.
     * </p>
     *
     * @param actionEvent The action that triggered the event
     */
    public void importDataset(ActionEvent actionEvent) {
        //Temporary fix to account for project not being selected in menu
        if((!(getWorkspaceService().getActiveWorkspaceItem() instanceof ProjectModel) && getProjectService().getProjects().size() > 0)){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Please Select a Project");
            alert.setHeaderText(null);
            alert.setContentText("Please select/highlight the project in the menu that you would like add your new dataset to.");
            alert.showAndWait();
            return;
        }
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Import Dataset");
        // Show only .txt and .csv files as file types
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Text files", "*.txt", "*.csv")
        );

        // Show the file dialog over the parent window
        File f = fileChooser.showOpenDialog(spreadsheet.getScene().getWindow());

        // If the user selected a file add it to the current active item as a dataset, ie. if the did not click cancel
        if (f != null) {
            DatasetModel dataset = new DatasetModel(f.getName(), f.getAbsolutePath());
            Displayable currentItem = getWorkspaceService().getActiveWorkspaceItem();
            setupNewDataset(currentItem, dataset);
        }
    }
}
