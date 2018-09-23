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
import javafx.beans.InvalidationListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyCombination;
import javafx.stage.FileChooser;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.val;
import org.iconic.project.dataset.DatasetModel;
import org.iconic.workspace.WorkspaceService;

import java.io.File;
import java.net.URL;
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
    public ProjectTreeController(final ProjectService projectService, final WorkspaceService workspaceService) {
        this.projectService = projectService;
        this.workspaceService = workspaceService;

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
            val root = new TreeItem<Displayable>();
            root.setExpanded(true);

            // Add every project as a child to the root node
            for (final ProjectModel p : getProjectService().getProjects()) {
                val child = new TreeItem<Displayable>(p);

                for (final DatasetModel d : p.getDatasets()) {
                    child.getChildren().add(new TreeItem<>(d));
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
            val item = cell.getValue();

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
            }
            // Otherwise display the item's withName
            else {
                setText(item.getLabel());

                // If the cell is for a ProjectModel give it a custom context menu
                if (item instanceof ProjectModel) {
                    // Add a menu item for importing datasets
                    val miImportDataset = new MenuItem("Import _Dataset...");
                    miImportDataset.setOnAction(this::importDataset);
                    miImportDataset.setMnemonicParsing(true);
                    miImportDataset.setAccelerator(KeyCombination.keyCombination("Shortcut+D"));

                    // Add a menu item for renaming the project
                    val miRenameProject = new MenuItem("_Rename...");
                    miRenameProject.setOnAction(this::renameProject);
                    miRenameProject.setMnemonicParsing(true);
                    miRenameProject.setAccelerator(KeyCombination.keyCombination("Shortcut+R"));

                    setContextMenu(
                            new ContextMenu(miImportDataset, miRenameProject)
                    );
                }
            }
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
                val dataset = new DatasetModel(f.getName(), f.getAbsolutePath());
                val item = getWorkspaceService().getActiveWorkspaceItem();

                // If the current active item isn't a project don't do anything
                if (item instanceof ProjectModel) {
                    val project = (ProjectModel) item;
                    val newProject = project.toBuilder().dataset(dataset).build();

                    getWorkspaceService().setActiveWorkspaceItem(null);
                    getProjectService().getProjects().set(getProjectService().getProjects().indexOf(project),(ProjectModel) newProject);
                    getWorkspaceService().setActiveWorkspaceItem(newProject);
                }
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
            val item = getWorkspaceService().getActiveWorkspaceItem();

            // If the current active item isn't a project don't do anything
            if (item instanceof ProjectModel) {
                val project = (ProjectModel) item;
                val defaultName = project.getLabel();

                // Open a text input dialog to get the new name and only do anything if the new name
                // is different
                TextInputDialog dialog = new TextInputDialog(defaultName);
                dialog.setTitle("Rename Project");
                dialog.setHeaderText("Project name");
                dialog.showAndWait().ifPresent(
                        name -> {
                            if (!name.trim().equals(project.getLabel())) {
                                val newProject = project.toBuilder().name(name).build();
                                getWorkspaceService().setActiveWorkspaceItem(null);
                                getProjectService().getProjects().set(getProjectService().getProjects().indexOf(project),(ProjectModel) newProject);
                                getWorkspaceService().setActiveWorkspaceItem(newProject);
                            }
                        }
                );
            }
        }
    }
}