package org.iconic.project.definition;

import com.google.inject.Inject;
import javafx.beans.InvalidationListener;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.fxml.Initializable;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.converter.NumberStringConverter;
import lombok.extern.log4j.Log4j2;

import java.net.URL;
import java.util.*;

import org.iconic.ea.data.DataManager;
import org.iconic.ea.operator.primitive.FunctionalPrimitive;
import org.iconic.project.BlockDisplay;
import org.iconic.project.Displayable;
import org.iconic.project.ProjectService;
import org.iconic.project.dataset.DatasetModel;
import org.iconic.project.search.SearchModel;
import org.iconic.workspace.WorkspaceService;

@Log4j2
public class DefineSearchController implements Initializable, DefineSearchService {

    private final ProjectService projectService;
    private final WorkspaceService workspaceService;

    @FXML
    public TableView<BlockDisplay> blockDisplayTableView;

    private HashMap<String, String> functionDefinitions;

    private static ArrayList<BlockDisplay> blockDisplays;

    @FXML
    private TextField tfTargetExpression;

    @Inject
    public DefineSearchController(final ProjectService projectService, final WorkspaceService workspaceService) {
        this.projectService = projectService;
        this.workspaceService = workspaceService;
        this.functionDefinitions = new HashMap<>();

        InvalidationListener selectionChangedListener = observable -> loadFunction();
        workspaceService.activeWorkspaceItemProperty().addListener(selectionChangedListener);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        tfTargetExpression.focusedProperty().addListener(focusListener);

        blockDisplays = new ArrayList<>(SearchModel.getFunctionalPrimitives().length);
        for (FunctionalPrimitive primitive :
                SearchModel.getFunctionalPrimitives()) {
            blockDisplays.add(new BlockDisplay(true, primitive.getSymbol(), 1));
        }

        blockDisplayTableView.setItems(FXCollections.observableArrayList(blockDisplays));

        TableColumn<BlockDisplay, String> nameCol = new TableColumn<>("Symbol");
        TableColumn<BlockDisplay, Boolean> enabledCol = new TableColumn<>("Enabled");
        TableColumn<BlockDisplay, Number> complexityCol = new TableColumn<>("Complexity");

        blockDisplayTableView.setEditable(true);
        enabledCol.setEditable(true);
        complexityCol.setEditable(true);

        complexityCol.setCellFactory(TextFieldTableCell.forTableColumn(new NumberStringConverter()));
        enabledCol.setCellFactory(CheckBoxTableCell.forTableColumn(enabledCol));

        nameCol.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        enabledCol.setCellValueFactory(cellData -> cellData.getValue().enabledProperty());
        complexityCol.setCellValueFactory(cellData -> cellData.getValue().complexityProperty());

        blockDisplayTableView.getColumns().addAll(enabledCol, nameCol, complexityCol);
    }

    @Override
    public SearchModel getSearchModel(DatasetModel datasetModel) {
        return new SearchModel(datasetModel, blockDisplays);
    }

    @Override
    public String getFunction() {
        String functionStr = null;

        Optional<DataManager<Double>> dataset = getDataManager();

        if (dataset.isPresent()) {
            // Get the ID of the dataset
            String[] splitString = dataset.toString().split("@");
            String datasetID = splitString[splitString.length - 1].replace("]", ""); // There's a trailing ']' from the toString

            // Get the dataset, if exists
            functionStr = functionDefinitions.get(datasetID);
        }

        return functionStr;
    }

    public void setFunction() {
        String functionStr = null;

        Optional<DataManager<Double>> dataset = getDataManager();

        if (dataset.isPresent()) {
            // Get the ID of the dataset
            String[] splitString = dataset.toString().split("@");
            String datasetID = splitString[splitString.length - 1].replace("]", ""); // There's a trailing ']' from the toString

            // Get the dataset, if exists
            functionStr = tfTargetExpression.getText();

            functionDefinitions.put(datasetID, functionStr);
        }
    }

    private void loadFunction() {
        Optional<DataManager<Double>> dataset = getDataManager();

        if (dataset.isPresent()) {
            // Get the ID of the dataset
            String[] splitString = dataset.toString().split("@");
            String datasetID = splitString[splitString.length - 1].replace("]", ""); // There's a trailing ']' from the toString

            // No need to redefine the function if one already exists, just insert instead
            String functionStr = functionDefinitions.get(datasetID);
            if (functionStr == null) {
                List<String> headers = dataset.get().getSampleHeaders();

                if (!headers.isEmpty()) {
                    functionStr = generateDefaultFunction(headers);

                    // Save the function defined in the hashmap of all the functions definitions
                    functionDefinitions.put(datasetID, functionStr);
                } else {
                    log.error("No headers found in this dataset");
                }
            }

            // NOTE(Meyer): Must check if not null otherwise injection will cause an NPE (it's dumb, I know)
            if (tfTargetExpression != null) {
                tfTargetExpression.setText(functionStr);
                dataset.get().defineFunction(functionStr);
            }
        }
    }

    private String generateDefaultFunction(List<String> headers) {
        // Get the last value in arraylist to get the target variable
        String functionResultStr = "(" + headers.get(headers.size() - 1) + ")";
        // Get the first value in list to start the function going
        StringBuilder functionDefinitionStr = new StringBuilder("(" + headers.get(0) + ")");

        // Get all the values bar the first and last column, which we already have
        for (int i = 1; i < headers.size() - 1; i++) {
            functionDefinitionStr.append(", (").append(headers.get(i)).append(")");
        }

        return functionResultStr + " = f(" + functionDefinitionStr + ")";
    }

    private Optional<DataManager<Double>> getDataManager() {
        Displayable item = workspaceService.getActiveWorkspaceItem();

        if (item instanceof DatasetModel) {
            DatasetModel dataset = (DatasetModel) item;
            return Optional.of(dataset.getDataManager());
        } else {
            return Optional.empty();
        }
    }

    private ChangeListener<Boolean> focusListener = new ChangeListener<Boolean>() {
        @Override
        public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
            if (!newValue) {
                // Get the data from the definition field, send it to the data manager to parse
                Optional<DataManager<Double>> dataset = getDataManager();

                String functionDefinition = tfTargetExpression.getText();

                if(dataset.isPresent()) {
                    setFunction();
                    dataset.get().defineFunction(functionDefinition);
                }
            }
        }
    };
}
