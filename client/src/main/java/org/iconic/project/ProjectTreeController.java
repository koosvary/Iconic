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

import com.google.inject.Inject;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.util.Pair;
import lombok.AccessLevel;
import lombok.Getter;
import org.iconic.config.IconService;
import org.iconic.project.dataset.DatasetModel;
import org.iconic.project.search.config.EvolutionaryAlgorithmType;
import org.iconic.project.search.config.SearchConfigurationModel;
import org.iconic.project.search.config.SearchConfigurationModelFactory;
import org.iconic.workspace.WorkspaceService;

import java.io.File;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * <p>
 * A controller for handling the project tree explorer view.
 * </p>
 * <p>
 * The ProjectTreeController maintains a tree view of the currently loaded datasets.
 * </p>
 */
public class ProjectTreeController implements Initializable {
    private final IconService iconService;
    private final ProjectService projectService;
    private final WorkspaceService workspaceService;

    @FXML
    @Getter(AccessLevel.PRIVATE)
    private TreeView<Displayable> projectView;

    /**
     * <p>
     * Constructs a new ProjectTreeController that attaches an invalidation listener onto the project service.
     * </p>
     */
    @Inject
    public ProjectTreeController(
            final ProjectService projectService,
            final WorkspaceService workspaceService,
            final IconService iconService
    ) {
        this.projectService = projectService;
        this.workspaceService = workspaceService;
        this.iconService = iconService;

        // Update the tree view whenever its backing model is invalidated
        InvalidationListener listener = observable -> updateTreeView();

        getProjectService().getProjects().addListener(listener);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Any user interface configuration that needs to happen at construction time must be done in this method to
     * guarantee that it's run after the user interface has been initialised.
     * </p>
     */
    @Override
    public void initialize(URL arg1, ResourceBundle arg2) {
        // Check that the tree view actually exists
        if (getProjectView() != null) {
            getProjectView().setCellFactory(p -> new ProjectItemTreeCellImpl());

            // Add a listener to check when the tree view's selection is changed and make the model match it
            getProjectView().getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) ->
                    setTreeViewSelection(newValue)
            );

            updateTreeView();
        }
    }

    /**
     * <p>
     * Updates the tree view based on the backing model.
     * </p>
     */
    private void updateTreeView() {
        // Check that the tree view actually exists
        if (getProjectView() != null) {
            TreeItem<Displayable> root = new TreeItem<>();
            root.setExpanded(true);

            // Add every project as a child to the root node
            for (final ProjectModel p : getProjectService().getProjects()) {
                TreeItem<Displayable> child = new TreeItem<>(p);

                for (final SearchConfigurationModel config : p.getSearchConfigurations()) {
                    TreeItem<Displayable> node = new TreeItem<>(config);
                    child.getChildren().add(node);
                }

                for (final DatasetModel dataset : p.getDatasets()) {
                    TreeItem<Displayable> node = new TreeItem<>(dataset);
                    child.getChildren().add(node);
                }

                child.setExpanded(true);

                root.getChildren().add(child);
            }

            getProjectView().setRoot(root);
            getProjectView().setShowRoot(false);
        }
    }

    /**
     * <p>Sets the current active dataset to the value within the provided tree view cell</p>
     *
     * @param cell The cell whose contents are to be set as the current active dataset
     */
    private void setTreeViewSelection(TreeItem<Displayable> cell) {
        if (cell != null) {
            Displayable item = cell.getValue();

            if (item != null) {
                getWorkspaceService().setActiveWorkspaceItem(item);
            }
        }
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

    private IconService getIconService() {
        return iconService;
    }

    /**
     * {@inheritDoc}
     * <p>
     * A ProjectItemTreeCellImpl defines a cell factory for formatting the contents of a tree view when the
     * cells contain project items.
     * </p>
     */
    private final class ProjectItemTreeCellImpl extends TreeCell<Displayable> {
        /**
         * {@inheritDoc}
         */
        @Override
        public void updateItem(Displayable item, boolean empty) {
            super.updateItem(item, empty);

            // If there's no item in the cell, don't display anything
            if (empty) {
                setText(null);
                setGraphic(null);
            }
            // Otherwise display the item's icon and name
            else {

                // Only display an icon if it has one
                item.getIcon().ifPresent(glyph ->
                        setGraphic(getIconService().getIcon(glyph))
                );

                setText(item.getLabel());

                // If the cell is for a ProjectModel give it a custom context menu
                if (item instanceof ProjectModel) {
                    // Add a menu item for adding a new search configuration
                    MenuItem miCreateSearch = createMenuItem(
                            "_Add Search Configuration...",
                            KeyCombination.keyCombination("Shortcut+A"),
                            this::createSearchConfiguration
                    );

                    // Add a menu item for importing datasets
                    MenuItem miImportDataset = createMenuItem(
                            "Import _Dataset...",
                            KeyCombination.keyCombination("Shortcut+D"),
                            this::importDataset
                    );

                    // Add a menu item for renaming the project
                    MenuItem miRenameProject = createMenuItem(
                            "_Rename...", KeyCombination.keyCombination("Shortcut+R"), this::renameProject
                    );

                    setContextMenu(
                            new ContextMenu(miCreateSearch, miImportDataset, miRenameProject)
                    );
                }
            }
        }

        MenuItem createMenuItem(
                final String label,
                final KeyCombination accelerator,
                final EventHandler<ActionEvent> actionEvent
        ) {
            MenuItem item = new MenuItem(label);
            item.setOnAction(actionEvent);
            item.setMnemonicParsing(true);
            item.setAccelerator(accelerator);
            return item;
        }

        /**
         * <p>
         * Opens a file dialog for choosing a dataset to import.
         * </p>
         *
         * @param actionEvent The action that triggered the event
         */
        private void importDataset(ActionEvent actionEvent) {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Import Dataset");
            // Show only .txt and .csv files
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Text files", "*.txt", "*.csv")
            );

            // Show the file dialog over the parent window
            File f = fileChooser.showOpenDialog(getTreeView().getScene().getWindow());

            // If the user selected a file add it to the current active item as a dataset
            if (f != null) {
                DatasetModel dataset = new DatasetModel(f.getName(), f.getAbsolutePath());
                Displayable item = getWorkspaceService().getActiveWorkspaceItem();

                // If the current active item isn't a project don't do anything
                if (item instanceof ProjectModel) {
                    ProjectModel project = (ProjectModel) item;
                    ProjectModel newProject = project.toBuilder().dataset(dataset).build();

                    getWorkspaceService().setActiveWorkspaceItem(null);
                    getProjectService().getProjects().set(getProjectService().getProjects().indexOf(project), newProject);
                    getWorkspaceService().setActiveWorkspaceItem(newProject);
                }
            }
        }

        /**
         * <p>
         * Opens a search configuration dialog for creating a new search configuration.
         * </p>
         *
         * <p>The search configuration dialog presents a combo box, text input field, and two buttons to the user.</p>
         *
         * @param actionEvent The action that triggered the event
         */
        private void createSearchConfiguration(ActionEvent actionEvent) {
            Dialog<Pair<String, EvolutionaryAlgorithmType>> dialog = new Dialog<>();
            dialog.setTitle("Add a Search Configuration");
            dialog.setHeaderText("Select the type of evolutionary algorithm you'd like to use");
//            dialog.initOwner(getStage());
            // Set the button types.
            ButtonType okButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(okButtonType, ButtonType.CANCEL);

            // Create a text field for the user to name the search configuration
            TextField configurationName = new TextField();
            configurationName.setPromptText("Configuration name");

            // Create a combo box holding all of the available evolutionary algorithms
            ObservableList<EvolutionaryAlgorithmType> options =
                    FXCollections.observableArrayList(
                            EvolutionaryAlgorithmType.CARTESIAN_GENETIC_PROGRAMMING,
                            EvolutionaryAlgorithmType.GENE_EXPRESSION_PROGRAMMING
                    );
            ComboBox<EvolutionaryAlgorithmType> availableAlgorithms = new ComboBox<>(options);

            availableAlgorithms.setPromptText("Select an evolutionary algorithm");

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
         * Opens a text input dialog for renaming the selected project.
         * </p>
         *
         * @param actionEvent The action that triggered the event
         */
        private void renameProject(ActionEvent actionEvent) {
            Displayable item = getWorkspaceService().getActiveWorkspaceItem();

            // If the current active item isn't a project don't do anything
            if (item instanceof ProjectModel) {
                ProjectModel project = (ProjectModel) item;
                String defaultName = project.getLabel();

                // Open a text input dialog to get the new name and only do anything if the new name
                // is different
                TextInputDialog dialog = new TextInputDialog(defaultName);
                dialog.setTitle("Rename Project");
                dialog.setHeaderText("Project name");
                dialog.showAndWait().ifPresent(
                        name -> {
                            if (!name.trim().equals(project.getLabel())) {
                                ProjectModel newProject = project.toBuilder().name(name).build();
                                getWorkspaceService().setActiveWorkspaceItem(null);
                                getProjectService().getProjects().set(getProjectService().getProjects().indexOf(project), newProject);
                                getWorkspaceService().setActiveWorkspaceItem(newProject);
                            }
                        }
                );
            }
        }
    }
}