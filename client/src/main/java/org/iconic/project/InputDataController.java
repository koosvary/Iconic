package org.iconic.project;

import javafx.beans.InvalidationListener;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.KeyCode;
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
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class InputDataController implements Initializable {
    private final ProjectService projectService;
    private final WorkspaceService workspaceService;

    @FXML
    private SpreadsheetView spreadsheet;

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
//            fillSpreadsheet2();
        }
        else{
            fillSpreadsheetByRow();
        }
    }



    private void clearUI() {
        spreadsheet.setGrid(new GridBase(0,0));
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        updateWorkspace();
    }

    private void fillSpreadsheetByRow(){
        Optional<DataManager<Double>> dataManager = getDataManager();
        int rowCount = dataManager.get().getSampleSize();
        int columnCount = dataManager.get().getFeatureSize();
        GridBase grid = new GridBase(rowCount, columnCount);

        ObservableList<ObservableList<SpreadsheetCell>> rows = FXCollections.observableArrayList();
        for (int row = 0; row < grid.getRowCount(); ++row) {
            final ObservableList<SpreadsheetCell> list = FXCollections.observableArrayList();
            for (int column = 0; column < grid.getColumnCount(); ++column) {
                String cellContents = String.valueOf(dataManager.get().getSampleRow(row).get(column));
                SpreadsheetCell nextCell = SpreadsheetCellType.STRING.createCell(row, column, 1, 1, cellContents);
                int changedRow = row;
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
        }
        grid.setRows(rows);

        spreadsheet.setGrid(grid);
    }

    private void fillSpreadsheet2(){
        int rowCount = 5;
        int columnCount = 3;
        GridBase grid = new GridBase(rowCount, columnCount);

        ObservableList<ObservableList<SpreadsheetCell>> rows = FXCollections.observableArrayList();
        for (int row = 0; row < grid.getRowCount(); ++row) {
            final ObservableList<SpreadsheetCell> list = FXCollections.observableArrayList();
            for (int column = 0; column < grid.getColumnCount(); ++column) {
//                list.add(SpreadsheetCellType.STRING.createCell(row, column, 1, 1,"Jack"));
                SpreadsheetCell nextCell = SpreadsheetCellType.STRING.createCell(row, column, 1, 1,"Change Me");
                nextCell.itemProperty().addListener((observable, oldValue, newValue) -> {
                    if (newValue instanceof String) {
                        //Do something
                        nextCell.setItem("Jack was here");
                    }
                });
                list.add(nextCell);
            }
            rows.add(list);
        }
        grid.setRows(rows);
        spreadsheet.setGrid(grid);
    }

    private void updateProjectDataset(int row, int column, Number newValue){
        Optional<DataManager<Double>> dataManager = getDataManager();
        dataManager.get().getSampleColumn(column).set(row,newValue);
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

    public void saveDataset(ActionEvent actionEvent) throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Dataset");
        // Show only .txt and .csv files
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Text files", "*.txt", "*.csv")
        );

        // Show the file dialog over the parent window
        File f = fileChooser.showSaveDialog(spreadsheet.getScene().getWindow());

        if(f != null){
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
            alert.setContentText("Please select/highlight the project in the menu that you would like to your dataset to.");
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
//                getProjectService().getProjects().remove(project);
//                getProjectService().getProjects().add(newProject);
                getWorkspaceService().setActiveWorkspaceItem(newProject);
            }
            else{
                //TODO If a project is not highlighted get the project associated with the current view
            }
        }
    }
}
