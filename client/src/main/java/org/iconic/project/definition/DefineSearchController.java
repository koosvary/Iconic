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
package org.iconic.project.definition;

import com.google.inject.Inject;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.VBox;
import javafx.util.converter.NumberStringConverter;
import lombok.extern.log4j.Log4j2;
import org.iconic.control.DatasetComboBox;

import java.net.URL;
import java.util.*;

import org.iconic.control.WorkspaceTab;
import org.iconic.ea.data.DataManager;
import org.iconic.project.BlockDisplay;
import org.iconic.project.Displayable;
import org.iconic.project.ProjectModel;
import org.iconic.project.ProjectService;
import org.iconic.project.dataset.DatasetModel;
import org.iconic.project.search.config.CgpConfigurationModel;
import org.iconic.views.ViewService;
import org.iconic.project.search.config.SearchConfigurationModel;
import org.iconic.project.search.io.SearchExecutor;
import org.iconic.workspace.WorkspaceService;

import java.io.IOException;
import java.util.stream.Collectors;

@Log4j2
public class DefineSearchController implements Initializable, DefineSearchService {

    private final ProjectService projectService;
    private final ViewService viewService;
    private final WorkspaceService workspaceService;

    @FXML
    private DatasetComboBox cbDatasets;

    @FXML
    VBox vbConfiguration;

    @FXML
    private WorkspaceTab defineTab;
    @FXML
    public TableView<BlockDisplay> blockDisplayTableView;

    @FXML
    public TextArea selectedBlockDisplayDescription;

    private HashMap<String, String> functionDefinitions;

    private List<BlockDisplay> blockDisplays;

    @FXML
    private TextField tfTargetExpression;

    final private Map<String, Node> configViews;

    @Inject
    public DefineSearchController(
            final ProjectService projectService,
            final ViewService viewService,
            final WorkspaceService workspaceService
    ) {
        this.projectService = projectService;
        this.viewService = viewService;
        this.workspaceService = workspaceService;
        this.functionDefinitions = new HashMap<>();
        this.blockDisplays = new ArrayList<>();
        this.configViews = new HashMap<>();

        InvalidationListener selectionChangedListener = observable -> updateTab();
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

        cbDatasets.valueProperty().addListener(this::updateDataset);
        defineTab.setOnSelectionChanged(event -> updateTab());

        tfTargetExpression.focusedProperty().addListener(focusListener);

        updateTab();
    }

    private void updateTab() {
        Displayable item = getWorkspaceService().getActiveWorkspaceItem();

        if (!(item instanceof SearchConfigurationModel)) {
            return;
        }

        SearchConfigurationModel search = (SearchConfigurationModel) item;
        blockDisplays = search.getPrimitives().entrySet().stream()
                .map(BlockDisplay::new)
                .sorted(Comparator
                        .comparing(BlockDisplay::getComplexity)
                        .thenComparing(BlockDisplay::getName)
                )
                .collect(Collectors.toList());

        Platform.runLater(() -> {
            Node node;
            vbConfiguration.getChildren().clear();
            blockDisplayTableView.setItems(FXCollections.observableArrayList(blockDisplays));

            if (search instanceof CgpConfigurationModel) {
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

                search.getDatasetModel().ifPresent(dataset ->
                        cbDatasets.getSelectionModel().select(dataset)
                );

                if (!search.getDatasetModel().isPresent()) {
                    cbDatasets.getSelectionModel().clearSelection();
                }
            } else {
                cbDatasets.setItems(FXCollections.emptyObservableList());
                cbDatasets.setPromptText("No datasets available");
                cbDatasets.setDisable(true);
            }

            vbConfiguration.getChildren().add(node);
        });
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
        String functionStr;
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

    private void updateDataset(ObservableValue<? extends DatasetModel> observer, DatasetModel oldValue, DatasetModel newValue) {
        Displayable item = getWorkspaceService().getActiveWorkspaceItem();

        if (!(item instanceof SearchConfigurationModel)) {
            return;
        }

        SearchConfigurationModel configModel = (SearchConfigurationModel) item;
        configModel.setDatasetModel(newValue);
        loadFunction();
    }

    private void loadFunction() {
        Optional<DataManager<Double>> dataset = getDataManager();

        if (dataset.isPresent()) {
            // Get the ID of the dataset
            String[] splitString = dataset.toString().split("@");
            String datasetID = splitString[splitString.length - 1].replace("]", ""); // There's a trailing ']' from the toString

            String functionStr = functionDefinitions.get(datasetID);
            List<String> headers = dataset.get().getSampleHeaders();

            if (!headers.isEmpty()) {
                functionStr = generateDefaultFunction(headers);

                // Save the function defined in the hashmap of all the functions definitions
                functionDefinitions.put(datasetID, functionStr);
            } else {
                log.error("No headers found in this dataset");
            }

            // NOTE(Meyer): Must check if not null otherwise injection will cause an NPE
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

        if (item instanceof SearchConfigurationModel) {
            SearchConfigurationModel search = (SearchConfigurationModel) item;
            if (search.getDatasetModel().isPresent()) {
                return Optional.of(search.getDatasetModel().get().getDataManager());
            }
        }

        return Optional.empty();
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
