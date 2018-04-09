package org.aiconic.menu;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import org.aiconic.model.DatasetModel;
import org.aiconic.model.GlobalModel;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * <p>
 * A controller for handling menu options.
 * </p>
 * <p>
 * The MenuController maintains the functionality available through the menu.
 * </p>
 */
public class MenuController implements Initializable {
    @FXML
    private StackPane pane;

    /**
     * <p>
     * Constructs a new MenuController
     * </p>
     */
    public MenuController() {
        // Do nothing
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
        // Do nothing
    }

    /**
     * <p>
     * Opens a file dialog for choosing a dataset to import.
     * </p>
     *
     * @param actionEvent The action that triggered the event
     */
    public void importDataset(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Import Dataset");
        // Show only .txt and .csv files
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Text files", "*.txt", "*.csv")
        );

        // Show the file dialog over the parent window
        File f = fileChooser.showOpenDialog(getPane().getScene().getWindow());

        // If the user selected a file add it to the global model as a dataset
        if (f != null) {
            final DatasetModel dataset = new DatasetModel(f.getName(), f.getAbsolutePath());
            GlobalModel.INSTANCE.getDatasets().add(dataset);
        }
    }

    /**
     * <p>
     * Closes all stages and exits the application.
     * </p>
     *
     * @param actionEvent The action that triggered this event
     */
    public void exit(ActionEvent actionEvent) {
        Platform.exit();
    }

    /**
     * <p>
     * Returns the root StackPane associated with this view.
     * </p>
     *
     * @return the root StackPane of this controller's view
     */
    private StackPane getPane() {
        return pane;
    }
}