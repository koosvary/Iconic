package org.aiconic.project;

import javafx.beans.InvalidationListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import org.aiconic.model.DatasetModel;
import org.aiconic.model.GlobalModel;

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
    @FXML
    private TreeView<DatasetModel> projectView;

    /**
     * <p>
     * Constructs a new ProjectTreeController that attaches an invalidation listener onto the global model.
     * </p>
     */
    public ProjectTreeController() {
        // Update the tree view whenever its backing model is invalidated
        InvalidationListener listener = observable -> {
            updateTreeView();
        };

        GlobalModel.INSTANCE.getDatasets().addListener(listener);
    }

    /**
     * <p>
     * Any user interface configuration that needs to happen at construction time must be done in this method to
     * guarantee that it's run after the user interface has been initialised.
     * </p>
     *
     * @param arg1
     * @param arg2
     */
    @Override
    public void initialize(URL arg1, ResourceBundle arg2) {
        projectView.setCellFactory(p -> new DatasetTreeCellImpl());
        projectView.setShowRoot(false);
        projectView.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) ->
                GlobalModel.INSTANCE.setActiveDataset(newValue.getValue())
        );

        updateTreeView();
    }

    /**
     * <p>
     * Updates the tree view based on the global model.
     * </p>
     */
    private void updateTreeView() {
        TreeItem<DatasetModel> root = new TreeItem<>();
        root.setExpanded(true);

        for (final DatasetModel d : GlobalModel.INSTANCE.getDatasets()) {
            root.getChildren().add(new TreeItem<>(d));
        }

        projectView.setRoot(root);
    }

    /**
     * <p>
     * A DatasetTreeCellImpl defines a cell factory for formatting the contents of a tree view.
     * </p>
     */
    private final class DatasetTreeCellImpl extends TreeCell<DatasetModel> {
        /**
         * <p>
         * Constructs a new DatasetTreeCellImpl.
         * </p>
         */
        DatasetTreeCellImpl() {
            // Do nothing
        }

        @Override
        public void updateItem(DatasetModel item, boolean empty) {
            super.updateItem(item, empty);

            // If there's no item in the cell, don't display anything
            if (empty) {
                setText(null);
            }
            // Otherwise display the item's name
            else {
                setText(item.getName());
            }
        }
    }
}