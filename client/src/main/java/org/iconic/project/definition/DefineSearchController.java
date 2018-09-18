package org.iconic.project.definition;

import com.google.inject.Inject;
import javafx.beans.InvalidationListener;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.converter.NumberStringConverter;
import lombok.extern.log4j.Log4j2;
import org.iconic.ea.data.DataManager;
import org.iconic.project.BlockDisplay;
import org.iconic.project.Displayable;
import org.iconic.project.ProjectService;
import org.iconic.project.dataset.DatasetModel;
import org.iconic.project.search.config.SearchConfigurationModel;
import org.iconic.project.search.SearchService;
import org.iconic.workspace.WorkspaceService;

import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

@Log4j2
public class DefineSearchController implements Initializable {

    private final ProjectService projectService;
    private final SearchService searchService;
    private final WorkspaceService workspaceService;

    @FXML
    public TableView<BlockDisplay> blockDisplayTableView;

    private HashMap<String, String> functionDefinitions;

    private List<BlockDisplay> blockDisplays;

    @FXML
    private TextField tfTargetExpression;

    @Inject
    public DefineSearchController(
            final ProjectService projectService,
            final SearchService searchService,
            final WorkspaceService workspaceService
    ) {
        this.projectService = projectService;
        this.searchService = searchService;
        this.workspaceService = workspaceService;
        this.functionDefinitions = new HashMap<>();
        this.blockDisplays = new ArrayList<>();

        InvalidationListener loadFunctionListener = observable -> loadFunction();
        InvalidationListener selectionChangedListener = observable -> updateTab();
        workspaceService.activeWorkspaceItemProperty().addListener(loadFunctionListener);
        workspaceService.activeWorkspaceItemProperty().addListener(selectionChangedListener);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Displayable item = getWorkspaceService().getActiveWorkspaceItem();

        if (!(item instanceof SearchConfigurationModel)) {
            return;
        }

        SearchConfigurationModel searchModel = (SearchConfigurationModel) item;

        // TODO: move primitives to the config model
        searchModel.getSearchExecutor().ifPresent(searchExecutor ->
                blockDisplays = searchExecutor.getFunctionalPrimitives().stream()
                        .map(BlockDisplay::new)
                        .collect(Collectors.toList())
        );

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

    private void updateTab() {
        Displayable item = getWorkspaceService().getActiveWorkspaceItem();

        if (!(item instanceof SearchConfigurationModel)) {
            return;
        }

        SearchConfigurationModel search = (SearchConfigurationModel) item;
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
}
