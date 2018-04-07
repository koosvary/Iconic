package org.aiconic;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import org.aiconic.io.DataManager;
import org.aiconic.metaheuristic.Trainer;
import org.aiconic.model.DatasetModel;
import org.aiconic.model.GlobalModel;

public class Bootstrapper extends Application {

    /**
     * <p>
     * Launches the application with the provided arguments.
     * </p>
     *
     * @param args The arguments to pass to the application
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     *
     * @param primaryStage
     */
    @Override
    public void start(Stage primaryStage) {
        // Create the primary stage
        Stage primaryStage1 = primaryStage;
        primaryStage.setTitle("- AIconic Workbench");

        // Create the root node for placing all the other components
        BorderPane root = new BorderPane();

        // Load the child UI elements from FXML resources
        FXMLLoader menuLoader = new FXMLLoader(getClass().getClassLoader().getResource("views/menu/MenuView.fxml"));
        FXMLLoader projectLoader = new FXMLLoader(getClass().getClassLoader().getResource("views/project/ProjectTreeView.fxml"));

        // Provide a localisation resource to use for i18n strings
        if (Locale.getDefault().getLanguage().startsWith("en")) {
            menuLoader.setResources(ResourceBundle.getBundle("localisation.en_au"));
            projectLoader.setResources(ResourceBundle.getBundle("localisation.en_au"));
        }
        // Just throw an exception for other languages (for now)
        else {
            throw new UnsupportedOperationException("Your language is not yet supported");
        }


        try {
            root.setTop(menuLoader.load());
            root.setLeft(projectLoader.load());
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }

        Scene scene = new Scene(root, 720, 480);
        // Load our stylesheets
        scene.getStylesheets().add(getClass().getClassLoader().getResource("stylesheet.css").toExternalForm());
        primaryStage.setScene(scene);

        primaryStage.setMaximized(true);
        primaryStage.show();
    }

    /**
     * Starts a search using the currently selected dataset.
     *
     * @param actionEvent
     *      The action that triggered this event.
     */
    @Deprecated
    private void startSearch(ActionEvent actionEvent) {
        DatasetModel dataset = GlobalModel.INSTANCE.getActiveDataset();

        if (dataset != null) {
            DataManager DM = new DataManager();
            DM.importData(dataset.getAbsolutePath());
            DM.normalizeScale();
            Trainer trainer = new Trainer();
            trainer.startSearch();
        }
    }
}