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
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.TextInputDialog;
import javafx.stage.FileChooser;
import lombok.val;
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

    private ScrollBar bar;

    @FXML
    private SpreadsheetView spreadsheet;
    @FXML
    private Button btnImportDataset;
    @FXML
    private Button btnExportDataset;

    @Inject
    public InputDataController(final ProjectService projectService, final WorkspaceService workspaceService) {
        this.projectService = projectService;
        this.workspaceService = workspaceService;

        // Update the workspace whenever the active dataset changes
        InvalidationListener selectionChangedListener = observable -> updateWorkspace();
        getWorkspaceService().activeWorkspaceItemProperty().addListener(selectionChangedListener);
    }

    private void updateWorkspace() {
        val item = getWorkspaceService().getActiveWorkspaceItem();

        // If no dataset is selected clear the UI
        if (!(item instanceof DatasetModel)) {
            clearUI();
        }
        else{
            fillSpreadsheetByRow();
            spreadsheet.setVisible(true);
        }
    }

    private void clearUI() {
        spreadsheet.setGrid(new GridBase(0,0));
        spreadsheet.setVisible(false);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //Binds the visibility of the spreadsheet to the export dataset button
        spreadsheet.managedProperty().bind(spreadsheet.visibleProperty());
        btnExportDataset.managedProperty().bind(btnExportDataset.visibleProperty());
        btnExportDataset.visibleProperty().bind(spreadsheet.visibleProperty());

        updateWorkspace();
    }

    private void fillSpreadsheetByRow(){
        Optional<DataManager<Double>> dataManager = getDataManager();
        int rowCount = dataManager.get().getSampleSize();
        int columnCount = dataManager.get().getFeatureSize();
        GridBase grid;
        if(dataManager.get().containsHeader()){
            grid = new GridBase(rowCount+2, columnCount);
        }
        else{
            grid = new GridBase(rowCount+1, columnCount);
        }
        ObservableList<ObservableList<SpreadsheetCell>> rows = FXCollections.observableArrayList();
        ObservableList<String> rowHeaders = FXCollections.observableArrayList();
        int row = 0;

        //Creates a row above the data for adding a column description
        ObservableList<SpreadsheetCell> infoList = FXCollections.observableArrayList();
        for (int column = 0; column < columnCount; ++column) {
            String cellContents = "Enter variable description here.";
            SpreadsheetCell nextCell = SpreadsheetCellType.STRING.createCell(row, column, 1, 1, cellContents);
            int finalColumn = column;
            nextCell.itemProperty().addListener((observable, oldValue, newValue) -> {
                try{
                    String newHeader = String.valueOf(newValue);
                    updateProjectHeaders(finalColumn,newHeader);
                }catch (Exception e) {
                    //handle exception here
                    nextCell.setItem(oldValue);
                }
            });
            infoList.add(nextCell);
        }
        rows.add(infoList);
        rowHeaders.add("info");
        row++;

        if(dataManager.get().containsHeader()){
            final ObservableList<SpreadsheetCell> list = FXCollections.observableArrayList();
            for (int column = 0; column < columnCount; ++column) {
                String cellContents = String.valueOf(dataManager.get().getSampleHeaders().get(column));
                SpreadsheetCell nextCell = SpreadsheetCellType.STRING.createCell(row, column, 1, 1, cellContents);
                int finalColumn = column;
                nextCell.itemProperty().addListener((observable, oldValue, newValue) -> {
                    try{
                        String newHeader = String.valueOf(newValue);
                        updateVariableDescriptions(finalColumn,newHeader);
                    }catch (Exception e) {
                        //handle exception here
                        nextCell.setItem(oldValue);
                    }
                });
                list.add(nextCell);
            }
            rows.add(list);
            rowHeaders.add("name");
            row++;
        }
        for(int cellRow = 0; cellRow < dataManager.get().getSampleSize(); cellRow++){
            final ObservableList<SpreadsheetCell> list = FXCollections.observableArrayList();
            for (int column = 0; column < grid.getColumnCount(); ++column) {
                String cellContents = String.valueOf(dataManager.get().getSampleRow(cellRow).get(column));
                SpreadsheetCell nextCell = SpreadsheetCellType.STRING.createCell(row, column, 1, 1, cellContents);
                int changedRow = cellRow;
                int changedColumn = column;
                nextCell.itemProperty().addListener((observable, oldValue, newValue) -> {
                    try{
                        Number newNumber = Double.parseDouble(String.valueOf(newValue));
                        updateProjectDataset(changedRow,changedColumn,newNumber);
                    }catch (Exception e) {
                        //handle exception here
                        nextCell.setItem(oldValue);
                    }
                });
                list.add(nextCell);
            }
            rows.add(list);
            rowHeaders.add(String.valueOf(cellRow+1));
            row++;
        }
        grid.setRows(rows);
        grid.getRowHeaders().setAll(rowHeaders);
        spreadsheet.setGrid(grid);
        spreadsheet.setRowHeaderWidth(50);
        bar = getVerticalScrollbar(spreadsheet);
        bar.valueProperty().addListener(this::scrolled);
    }

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


    private void scrolled(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
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
     * Adds a new row to a given SpreadsheetView's Grid on runtime, preserving all it's cells values.
     */
    private void spreadsheetAddRow() {
        Grid oldGrid = spreadsheet.getGrid();

        int newRowPos = oldGrid.getRowCount();
        ObservableList<String> rowHeaders = oldGrid.getRowHeaders();
        ObservableList<ObservableList<SpreadsheetCell>> rows = oldGrid.getRows();

        final ObservableList<SpreadsheetCell> list = FXCollections.observableArrayList();
        for (int column = 0; column < oldGrid.getColumnCount(); ++column) {
            String cellContents = "0.0";
            SpreadsheetCell nextCell = SpreadsheetCellType.STRING.createCell(newRowPos, column, 1, 1, cellContents);
            nextCell.itemProperty().addListener((observable, oldValue, newValue) -> {
                try{
                    addNewRowsFromSpreadsheet(newRowPos);
                }catch (Exception e) {
                    //handle exception here
                    nextCell.setItem(oldValue);
                }
            });
            list.add(nextCell);
        }
        // Adds the new row to the rows set
        rows.add(list);
        rowHeaders.add(String.valueOf(newRowPos+1));
        // Updates the Grid rows
        oldGrid.getRowHeaders().setAll(rowHeaders);
        spreadsheet.setGrid(oldGrid);
    }

    private void addNewRowsFromSpreadsheet(int row){
        Optional<DataManager<Double>> dataManager = getDataManager();
        int datasetSize = dataManager.get().getSampleSize();
        int datasetFeatureSize = dataManager.get().getFeatureSize();
        int newDatatsetSize = row;


        ObservableList<ObservableList<SpreadsheetCell>> rows = FXCollections.observableArrayList();

        while(datasetSize<=newDatatsetSize){
            List<Number> newDataValues = new ArrayList<>();
            for (int column = 0; column < datasetFeatureSize; ++column) {
                SpreadsheetCell oldCell = spreadsheet.getGrid().getRows().get(datasetSize).get(column);
                String cellContents = String.valueOf(oldCell.getItem());
                SpreadsheetCell newCell = SpreadsheetCellType.STRING.createCell(row, column, 1, 1, cellContents);
                int changedRow = datasetSize;
                int changedColumn = column;
                newCell.itemProperty().addListener((observable, oldValue, newValue) -> {
                    try{
                        Number newNumber = Double.parseDouble(String.valueOf(newValue));
                        updateProjectDataset(changedRow,changedColumn,newNumber);
                    }catch (Exception e) {
                        //handle exception here
                        newCell.setItem(oldValue);
                    }
                });
                spreadsheet.getGrid().getRows().get(datasetSize).set(column,newCell);
                newDataValues.add(Double.parseDouble(cellContents));
            }
            addRowToDataset(newDataValues);
            datasetSize++;
        }
    }

    private void addRowToDataset(List<Number> newNumbers){
        Optional<DataManager<Double>> dataManager = getDataManager();
        dataManager.get().addRow(newNumbers);
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
    }

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
        // Show only .txt and .csv files
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Text files", "*.txt", "*.csv")
        );

        // Show the file dialog over the parent window
        File f = fileChooser.showOpenDialog(spreadsheet.getScene().getWindow());

        // If the user selected a file add it to the current active item as a dataset
        if (f != null) {
            DatasetModel dataset = new DatasetModel(f.getName(), f.getAbsolutePath());
            Displayable currentItem = getWorkspaceService().getActiveWorkspaceItem();
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
            //If statement required to check if user clicked cancel in previous dialog
            if (getWorkspaceService().getActiveWorkspaceItem() instanceof ProjectModel) {
                ProjectModel project = (ProjectModel) getWorkspaceService().getActiveWorkspaceItem();
                ProjectModel newProject = project.toBuilder().dataset(dataset).build();
                getWorkspaceService().setActiveWorkspaceItem(null);
                getProjectService().getProjects().set(getProjectService().getProjects().indexOf(project),newProject);
                getWorkspaceService().setActiveWorkspaceItem(newProject);
            }
            //TODO If a project is not highlighted get the project associated with the current view
        }
    }
}
