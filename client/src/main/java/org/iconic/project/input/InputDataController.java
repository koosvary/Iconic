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
package org.iconic.project.input;

import javafx.application.Platform;
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
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.util.Pair;
import org.controlsfx.control.spreadsheet.*;
import org.iconic.control.WorkspaceTab;
import org.iconic.ea.data.DataManager;
import org.iconic.project.Displayable;
import org.iconic.project.ProjectModel;
import org.iconic.project.ProjectService;
import org.iconic.project.dataset.DatasetModel;
import org.iconic.project.search.config.EvolutionaryAlgorithmType;
import org.iconic.project.search.config.SearchConfigurationModel;
import org.iconic.project.search.config.SearchConfigurationModelFactory;
import org.iconic.workspace.WorkspaceService;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;

/**
 * A controller class for handling the InputData view.
 * <p>
 * The InputDataController provides a bridge between inputting/importing data from the GUI and updating
 * the data within the DataManager.
 */
public class InputDataController implements Initializable {
    private final ProjectService projectService;
    private final WorkspaceService workspaceService;

    private ObservableList<String> rowHeaders = FXCollections.observableArrayList();
    private String infoPlaceholder = "Enter variable description here";

    @FXML
    private WorkspaceTab inputTab;
    @FXML
    private SpreadsheetView spreadsheet;
    @FXML
    private Button btnCreateDataset;
    @FXML
    private Button btnImportDataset;
    @FXML
    private Button btnExportDataset;
    @FXML
    private HBox searchButtonHBox;
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
            searchButtonHBox.setVisible(false);
            createButtonHBox.setVisible(false);
            importButtonHBox.setVisible(false);
            welcomeMessage.setText("Save this dataset to a file.");
            setupContextMenu();
        }
    }

    /**
     * Hides the spreadsheet and export spreadsheet button to show what would be classified as the home page.
     */
    private void clearUI() {
        Displayable item = getWorkspaceService().getActiveWorkspaceItem();
        if(item instanceof ProjectModel){
            searchButtonHBox.setVisible(true);
        }
        else{
            searchButtonHBox.setVisible(false);
        }
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

        inputTab.setOnSelectionChanged(event -> updateWorkspace());
    }

    /**
     * Sets up the right click menu for copying and pasting
     */
    private void setupContextMenu(){
        ContextMenu contextMenu = new ContextMenu();
        MenuItem copy = new MenuItem("Copy");
        copy.setAccelerator(KeyCombination.keyCombination("Ctrl+C"));
        copy.setOnAction(actionEvent -> {
            copySelectionToClipboard();
        });
        MenuItem paste = new MenuItem("Paste");
        paste.setAccelerator(KeyCombination.keyCombination("Ctrl+V"));
        paste.setOnAction(actionEvent -> {
            extendWithClipboard();
            pasteFromClipboard();
        });
        MenuItem delete = new MenuItem("Delete");
        delete.setAccelerator(KeyCombination.keyCombination("Delete"));
        delete.setOnAction(actionEvent -> {
            deleteSelection();

        });
        contextMenu.getItems().addAll(copy,paste,delete);
        spreadsheet.setContextMenu(contextMenu);
    }

    /**
     * Reads the selected cells and places them in the clipboard formatted as a spreadsheet
     */
    private void copySelectionToClipboard() {
        StringBuilder clipboardString = new StringBuilder();

        ObservableList<TablePosition> positionList = spreadsheet.getSelectionModel().getSelectedCells();

        int prevRow = -1;

        for (TablePosition position : positionList) {

            int row = position.getRow();
            int col = position.getColumn();

            // determine whether we advance in a row (tab) or a column
            // (newline).
            if (prevRow == row) {
                clipboardString.append('\t');
            }
            else if (prevRow != -1) {
                clipboardString.append('\n');
            }

            Object observableValue = spreadsheet.getGrid().getRows().get(row).get(col).getItem();

            if(observableValue != null){
                // add new item to clipboard
                clipboardString.append(String.valueOf(observableValue));
            }

            // remember previous
            prevRow = row;
        }

        // create clipboard content
        final ClipboardContent clipboardContent = new ClipboardContent();
        clipboardContent.putString(clipboardString.toString());

        // set clipboard content
        Clipboard.getSystemClipboard().setContent(clipboardContent);
    }

    /**
     * If there is excel cells in the clipboard they will be pasted into the current cells
     */
    private void pasteFromClipboard() {
        // abort if there's not cell selected to start with
        if(spreadsheet.getSelectionModel().getSelectedCells().size() == 0) {
            return;
        }

        // get the cell position to start with
        TablePosition pasteCellPosition = spreadsheet.getSelectionModel().getSelectedCells().get(0);

        String pasteString = Clipboard.getSystemClipboard().getString();

        int rowClipboard = -1;


        Scanner s = new Scanner(pasteString);
        while (s.hasNextLine()) {
            rowClipboard++;
            String line = s.nextLine();
            String[] items= line.split("\t", -1);
            int colClipboard = -1;
            for(int i = 0; i < items.length; i++){
                colClipboard++;
                String clipboardCellContent = items[i];
                // calculate the position in the table cell
                int rowTable = pasteCellPosition.getRow() + rowClipboard;
                int colTable = pasteCellPosition.getColumn() + colClipboard;

                // skip if we reached the end of the table
                if(rowTable >= spreadsheet.getItems().size()) {
                    continue;
                }
                if(colTable >= spreadsheet.getColumns().size()) {
                    continue;
                }

                if(rowTable < 2){
                    spreadsheet.getGrid().getRows().get(rowTable).get(colTable).setItem(String.valueOf(clipboardCellContent));
                }
                else {
                    // get cell
                    try {
                        double content = Double.parseDouble(clipboardCellContent);
                        spreadsheet.getGrid().getRows().get(rowTable).get(colTable).setItem(String.valueOf(content));
                    } catch (Exception ignored) {
                        spreadsheet.getGrid().getRows().get(rowTable).get(colTable).setItem(null);
                        updateProjectDataset(rowTable-2,colTable,null);
                    }
                }

            }
        }

    }

    /**
     * Finds the selected cells and deletes the values within them
     */
    private void deleteSelection() {
        ObservableList<TablePosition> positionList = spreadsheet.getSelectionModel().getSelectedCells();

        for (TablePosition position : positionList) {
            int row = position.getRow();
            int col = position.getColumn();

            spreadsheet.getGrid().getRows().get(row).get(col).setItem(null);

            updateProjectDataset(row-2,col,null);
        }
    }

    /**
     * Calculates the size of the extended spreadsheet after the clipboard will be pasted
     */
    private void extendWithClipboard() {

        int selectedRow = spreadsheet.getGrid().getRowCount() - 1;
        int selectColumn = spreadsheet.getGrid().getColumnCount() - 1;

        // abort if there's not cell selected to start with
        if(spreadsheet.getSelectionModel().getSelectedCells().size() != 0) {
            TablePosition pasteCellPosition = spreadsheet.getSelectionModel().getSelectedCells().get(0);

            selectedRow = pasteCellPosition.getRow();
            selectColumn = pasteCellPosition.getColumn();
        }

        int spreadsheetRowCount = spreadsheet.getGrid().getRowCount();
        int spreadsheetColumnCount = spreadsheet.getGrid().getColumnCount();

        String pasteString = Clipboard.getSystemClipboard().getString();


        StringTokenizer rowTokenizer = new StringTokenizer( pasteString, "\n");
        if(rowTokenizer.hasMoreTokens()){

            int clipboardRowCount;
            int clipboardColumnCount;

            clipboardRowCount = rowTokenizer.countTokens();
            String rowString = rowTokenizer.nextToken();
            StringTokenizer columnTokenizer = new StringTokenizer(rowString, "\t");
            clipboardColumnCount = columnTokenizer.countTokens();

            int extendedRowPos = selectedRow + clipboardRowCount;
            int extendedColumnPos = selectColumn + clipboardColumnCount;

            if(extendedRowPos > spreadsheetRowCount && extendedColumnPos > spreadsheetColumnCount) {
                extendGrid(extendedRowPos, extendedColumnPos);
            }
            else if(extendedRowPos > spreadsheetRowCount){
                extendGrid(extendedRowPos, spreadsheetColumnCount);
            }
            else if(extendedColumnPos > spreadsheetColumnCount){
                extendGrid(spreadsheetRowCount, extendedColumnPos);
            }
        }
    }

    /**
     * Given a new row and column count will extend the spreadsheet's size
     *
     * @param newRowSize
     * @param newColumnSize
     */
    private void extendGrid(int newRowSize, int newColumnSize){
        Grid oldGrid = spreadsheet.getGrid();
        //The column index where the new column will be added
        ObservableList<ObservableList<SpreadsheetCell>> rows = FXCollections.observableArrayList();
        rows.addAll(oldGrid.getRows());

        int oldRowCount = oldGrid.getRowCount();
        int oldColumnCount = oldGrid.getColumnCount();

        for(int currentColumn = oldColumnCount;  currentColumn < newColumnSize; currentColumn++) {
            //Create and adds the new column description cell with an appropriate change listener
            SpreadsheetCell infoCell = SpreadsheetCellType.STRING.createCell(0, currentColumn, 1, 1, infoPlaceholder);
            int finalCurrentColumn = currentColumn;
            infoCell.itemProperty().addListener((observable, oldValue, newValue) -> {
                try {
                    String newHeader = String.valueOf(newValue);
                    addNewColumnsFromSpreadsheet(0, finalCurrentColumn);
                    updateVariableDescriptions(finalCurrentColumn, newHeader);
                } catch (Exception e) {
                    infoCell.setItem(oldValue);
                }
            });
            rows.get(0).add(infoCell);

            //Create and adds the new column name cell with an appropriate change listener
            SpreadsheetCell headerCell = SpreadsheetCellType.STRING.createCell(1, currentColumn, 1, 1, getDataManager().get().intToHeader(currentColumn));
            int finalCurrentColumn1 = currentColumn;
            headerCell.itemProperty().addListener((observable, oldValue, newValue) -> {
                try {
                    String newHeader = String.valueOf(newValue);
                    addNewColumnsFromSpreadsheet(1, finalCurrentColumn1);
                    updateProjectHeaders(finalCurrentColumn1, newHeader);
                } catch (Exception e) {
                    headerCell.setItem(oldValue);
                }
            });
            rows.get(1).add(headerCell);
        }


        //Adds an empty cell to each new row, this loop starts at 2 to account for the previous two cells already being added
        for (int row = 2; row < oldRowCount; ++row) {
            for(int currentColumn = oldColumnCount;  currentColumn < newColumnSize; currentColumn++){
                String cellContents = "";
                SpreadsheetCell nextCell = SpreadsheetCellType.STRING.createCell(row, currentColumn, 1, 1, cellContents);
                //Set unused cell to be grey
                nextCell.setStyle("-fx-background-color: #dcdcdc;");
                int finalRow = row;
                //When a value is added to this new cell, add the columns up to and including the column of this cell
                int finalCurrentColumn = currentColumn;
                nextCell.itemProperty().addListener((observable, oldValue, newValue) -> {
                    try{
                        //Check that entered value is a number
                        Double.parseDouble(String.valueOf(newValue));
                        addNewColumnsFromSpreadsheet(finalRow, finalCurrentColumn);
                    }catch (Exception e) {
                        if(!newValue.equals("")) {
                            nextCell.setItem(oldValue);
                        }
                    }
                });
                rows.get(row).add(nextCell);
            }
        }
        int currentRow = oldRowCount;
        rowHeaders = FXCollections.observableArrayList();
        rowHeaders.addAll(oldGrid.getRowHeaders());
        while(currentRow < newRowSize){
            ObservableList<SpreadsheetCell> list = FXCollections.observableArrayList();
            for(int currentColumn = 0; currentColumn < newColumnSize; currentColumn ++){
                String cellContents = "";
                SpreadsheetCell nextCell = SpreadsheetCellType.STRING.createCell(currentColumn, currentColumn, 1, 1, cellContents);
                //Set unused cell to be grey
                nextCell.setStyle("-fx-background-color: #dcdcdc;");
                int finalColumn = currentColumn;
                int finalCurrentRow = currentRow;
                //When a value is added to the new cell add the rows up to and including the row of this cell
                nextCell.itemProperty().addListener((observable, oldValue, newValue) -> {
                    try{
                        //Check that entered value is a number
                        Double.parseDouble(String.valueOf(newValue));
                        addNewRowsFromSpreadsheet(finalCurrentRow, finalColumn);
                    }catch (Exception e) {
                        if(!newValue.equals("")) {
                            nextCell.setItem(oldValue);
                        }
                    }
                });
                list.add(nextCell);
            }
            rows.add(list);
            rowHeaders.add(String.valueOf(currentRow+1));
            currentRow++;
        }
        //Create a new grid a column wider than the current grid
        //This needs to be done as there is no method to add a new column to a grid base
        Grid newGrid = new GridBase(rows.size(),newColumnSize);
        newGrid.setRows(rows);
        newGrid.getRowHeaders().setAll(rowHeaders);

        //Change the existing grid to the new wider grid
        spreadsheet.setGrid(newGrid);
        spreadsheet.setRowHeaderWidth(50);
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
        //If the datset has headers they are added to the spreadsheet before the data
        if(dataManager.get().containsInfo()) {
            ObservableList<SpreadsheetCell> infoList = FXCollections.observableArrayList();
            for (int column = 0; column < datasetColumnCount; ++column) {
                String cellContents = String.valueOf(dataManager.get().getSampleInfo().get(column));
                SpreadsheetCell nextCell = SpreadsheetCellType.STRING.createCell(spreadsheetRow, column, 1, 1, cellContents);
                int finalColumn = column;
                //If the description is changed update it in the dataset
                nextCell.itemProperty().addListener((observable, oldValue, newValue) -> {
                    try {
                        String newHeader = String.valueOf(newValue);
                        updateVariableDescriptions(finalColumn, newHeader);
                    } catch (Exception e) {
                        nextCell.setItem(oldValue);
                    }
                });
                infoList.add(nextCell);
            }
            rows.add(infoList);
            rowHeaders.add("info");
            spreadsheetRow++;
        }
        else {
            ObservableList<SpreadsheetCell> infoList = FXCollections.observableArrayList();
            for (int column = 0; column < datasetColumnCount; ++column) {
                String cellContents = String.valueOf(infoPlaceholder);
                SpreadsheetCell nextCell = SpreadsheetCellType.STRING.createCell(spreadsheetRow, column, 1, 1, cellContents);
                int finalColumn = column;
                //If the description is changed update it in the dataset
                nextCell.itemProperty().addListener((observable, oldValue, newValue) -> {
                    try {
                        String newHeader = String.valueOf(newValue);
                        updateVariableDescriptions(finalColumn, newHeader);
                    } catch (Exception e) {
                        nextCell.setItem(oldValue);
                    }
                });
                infoList.add(nextCell);
            }
            rows.add(infoList);
            rowHeaders.add("info");
            spreadsheetRow++;
        }

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
                if(cellContents.equals("null")){
                    cellContents = null;
                }
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
            addColumnToDataset(spreadsheet.getGrid().getRows().get(0).get(currentNewColPos).getText(),spreadsheet.getGrid().getRows().get(1).get(currentNewColPos).getText(),newDataValues);
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
        SpreadsheetCell newCell = SpreadsheetCellType.STRING.createCell(row, column, 1, 1, cellContents);
        //Minus two accounting for header and description
        int changedRow = row - 2;
        addChangeListenerToCell(newCell,changedRow, column);
        spreadsheet.getGrid().getRows().get(row).set(column,newCell);
        if(cellContents.isEmpty()){
            newDataValues.add(null);
        }
        else {
            newDataValues.add(Double.parseDouble(cellContents));
        }
    }

    /**
     * Adds a change listener to the given cell that will ensure the dataset is
     * updated when the cell is changed.
     */
    private void addChangeListenerToCell(SpreadsheetCell spreadsheetCell, int row, int column){
        spreadsheetCell.itemProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue != null) {
                try {
                    Number newNumber = Double.parseDouble(String.valueOf(newValue));
                    updateProjectDataset(row, column, newNumber);
                } catch (Exception e) {
                    spreadsheetCell.setItem(oldValue);
                }
            }
        });
    }

    private void addRowToDataset(List<Number> newNumbers){
        Optional<DataManager<Double>> dataManager = getDataManager();
        dataManager.get().addRow(newNumbers);
    }

    private void addColumnToDataset(String info, String header, List<Number> newNumbers){
        Optional<DataManager<Double>> dataManager = getDataManager();
        dataManager.get().addNewFeature(info,header, newNumbers);
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
        dataManager.get().updateInfoAtIndex(column,newValue);
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
     *
     *
     * @return the workspace service of the controller
     */
    private WorkspaceService getWorkspaceService() {
        return workspaceService;
    }

    /**
     * <p>
     * Returns the project service of this controller
     *
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
                        if (!name.isEmpty()) {
                            final ProjectModel project = ProjectModel.builder().name(name).build();
                            getWorkspaceService().setActiveWorkspaceItem(project);
                            getProjectService().getProjects().add(project);
                        }
                    }
            );
            newSearch(new ActionEvent());
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
     * Opens a search configuration dialog for creating a new search configuration.
     *
     *
     * <p>The search configuration dialog presents a combo box, text input field, and two buttons to the user.
     *
     * @param actionEvent The action that triggered the event
     */
    public void newSearch(ActionEvent actionEvent) {
        Dialog<Pair<String, EvolutionaryAlgorithmType>> dialog = new Dialog<>();
        dialog.setTitle("Add a Search Configuration");
        dialog.setHeaderText("Select the type of evolutionary algorithm you'd like to use");
//            dialog.initOwner(getStage());
        // Set the button types.
        ButtonType okButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(okButtonType, ButtonType.CANCEL);

        // Create a text field for the user to name the search configuration
        TextField configurationName = new TextField("Search1");
        configurationName.setPromptText("Configuration name");

        // Create a combo box holding all of the available evolutionary algorithms
        ObservableList<EvolutionaryAlgorithmType> options =
                FXCollections.observableArrayList(
                        EvolutionaryAlgorithmType.CARTESIAN_GENETIC_PROGRAMMING,
                        EvolutionaryAlgorithmType.GENE_EXPRESSION_PROGRAMMING
                );
        ComboBox<EvolutionaryAlgorithmType> availableAlgorithms = new ComboBox<>(options);

        availableAlgorithms.getSelectionModel().selectFirst();

        VBox contentArea = new VBox();
        contentArea.getChildren().add(configurationName);
        contentArea.getChildren().add(availableAlgorithms);
        contentArea.setSpacing(10);

        dialog.getDialogPane().setContent(contentArea);

        // Set the user's default focus to the text field
        Platform.runLater(configurationName::requestFocus);

        // We need to convert the result from the dialog into a name-type pair when a button is clicked
        dialog.setResultConverter(buttonType -> {
                    if (buttonType == okButtonType) {
                        return new Pair<>(configurationName.getText(), availableAlgorithms.getValue());
                    }
                    // If they click any other button just return null
                    return null;
                }
        );

        Optional<Pair<String, EvolutionaryAlgorithmType>> result = dialog.showAndWait();

        Displayable item = getWorkspaceService().getActiveWorkspaceItem();

        // If the current active item isn't a project don't do anything
        if (item instanceof ProjectModel) {
            result.ifPresent(params -> {
                        SearchConfigurationModelFactory searchConfigurationModelFactory =
                                new SearchConfigurationModelFactory();

                        if (
                                params.getKey() == null ||
                                        params.getValue() == null ||
                                        params.getKey().trim().isEmpty()
                        ) {
                            return;
                        }

                        // Construct the search configuration model using the parameters
                        // input by the user
                        SearchConfigurationModel searchConfiguration =
                                searchConfigurationModelFactory.getSearchConfigurationModel(
                                        params.getKey(), params.getValue()
                                );

                        // Clone the selected project model and add the configuration to it
                        ProjectModel project = (ProjectModel) item;
                        ProjectModel newProject = project.toBuilder()
                                .searchConfiguration(searchConfiguration)
                                .build();

                        // Update the active workspace item to the new project (modified)
                        getWorkspaceService().setActiveWorkspaceItem(null);
                        getProjectService().getProjects()
                                .set(getProjectService().getProjects().indexOf(project), newProject);
                        getWorkspaceService().setActiveWorkspaceItem(newProject);
                    }
            );
        }
    }


    /**
     * <p>
     * Opens a file dialog for choosing a dataset to import.
     *
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
