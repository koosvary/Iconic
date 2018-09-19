package org.iconic.project.definition;

import com.google.inject.Inject;
import javafx.beans.InvalidationListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import javafx.util.converter.NumberStringConverter;
import lombok.extern.log4j.Log4j2;
import org.iconic.control.DatasetComboBox;
import org.iconic.control.operator.evolutionary.MutatorComboBox;
import org.iconic.ea.data.DataManager;
import org.iconic.project.BlockDisplay;
import org.iconic.project.Displayable;
import org.iconic.project.ProjectModel;
import org.iconic.project.ProjectService;
import org.iconic.project.dataset.DatasetModel;
import org.iconic.project.dataset.DatasetService;
import org.iconic.project.search.SearchService;
import org.iconic.project.search.config.CgpConfigurationModel;
import org.iconic.project.search.config.SearchConfigurationModel;
import org.iconic.views.ViewService;
import org.iconic.workspace.WorkspaceService;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

@Log4j2
public class DefineSearchController implements Initializable {

    private final ProjectService projectService;
    private final SearchService searchService;
    private final ViewService viewService;
    private final WorkspaceService workspaceService;

    @FXML
    private DatasetComboBox cbDatasets;

    @FXML
    public MutatorComboBox cbMutators;

    @FXML
    VBox vbConfiguration;

    @FXML
    public TableView<BlockDisplay> blockDisplayTableView;

    private HashMap<String, String> functionDefinitions;

    private List<BlockDisplay> blockDisplays;

    @FXML
    private TextField tfTargetExpression;

    final private Map<String, Node> configViews;

    @Inject
    public DefineSearchController(
            final ProjectService projectService,
            final SearchService searchService,
            final ViewService viewService,
            final WorkspaceService workspaceService
    ) {
        this.projectService = projectService;
        this.searchService = searchService;
        this.viewService = viewService;
        this.workspaceService = workspaceService;
        this.functionDefinitions = new HashMap<>();
        this.blockDisplays = new ArrayList<>();
        this.configViews = new HashMap<>();

        InvalidationListener loadFunctionListener = observable -> loadFunction();
        InvalidationListener selectionChangedListener = observable -> updateTab();
        workspaceService.activeWorkspaceItemProperty().addListener(loadFunctionListener);
        workspaceService.activeWorkspaceItemProperty().addListener(selectionChangedListener);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            getConfigViews().put("cgp-config", getViewService().getViews().get("cgp-config").load());
            getConfigViews().put("gep-config", getViewService().getViews().get("gep-config").load());
        } catch (IOException ex) {
            // TODO: display error screen to the user
            log.error("{}: {}", ex::getMessage, ex::getStackTrace);
        }

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

        updateTab();
    }

    private void updateTab() {
        Displayable item = getWorkspaceService().getActiveWorkspaceItem();

        if (!(item instanceof SearchConfigurationModel)) {
            return;
        }

        SearchConfigurationModel configModel = (SearchConfigurationModel) item;

        blockDisplays = configModel.getPrimitives().stream()
                .map(BlockDisplay::new)
                .collect(Collectors.toList());

        blockDisplayTableView.setItems(FXCollections.observableArrayList(blockDisplays));

        Node node;
        vbConfiguration.getChildren().clear();

        if (configModel instanceof CgpConfigurationModel) {
            node = getConfigViews().get("cgp-config");
        } else {
            node = getConfigViews().get("gep-config");
        }

        // Add all of the datasets within the project to the datasets combo box
        Optional<ProjectModel> parent = getProjectService().findParentProject(item);

        if (parent.isPresent() && parent.get().getDatasets().size() > 0) {
            ObservableList<DatasetModel> options = parent.get().getDatasets();
            cbDatasets.setItems(options);
            cbDatasets.setPromptText("Select a dataset");
            cbDatasets.setDisable(false);
        } else {
            cbDatasets.setItems(FXCollections.emptyObservableList());
            cbDatasets.setPromptText("No datasets available");
            cbDatasets.setDisable(true);
        }

        vbConfiguration.getChildren().add(node);
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
     * <p>Returns the view service of this controller</p>
     *
     * @return the view service of the controller
     */
    private ViewService getViewService() {
        return viewService;
    }

    /**
     * <p>Returns the workspace service of this controller</p>
     *
     * @return the workspace service of the controller
     */
    private WorkspaceService getWorkspaceService() {
        return workspaceService;
    }

    /**
     * <p>Returns the project service of this controller</p>
     *
     * @return the project service of the controller
     */
    private ProjectService getProjectService() {
        return projectService;
    }

    private Map<String, Node> getConfigViews() {
        return configViews;
    }
}
