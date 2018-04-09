package org.aiconic;

import java.io.IOException;
import java.net.URL;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.UUID;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import org.aiconic.model.GlobalModel;
import org.aiconic.model.SearchModel;

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
        FXMLLoader workspaceLoader = new FXMLLoader(getClass().getClassLoader().getResource("views/workspace/WorkspaceView.fxml"));

        // Provide a localisation resource to use for i18n strings
        if (Locale.getDefault().getLanguage().startsWith("en")) {
            menuLoader.setResources(ResourceBundle.getBundle("localisation.en_au"));
            projectLoader.setResources(ResourceBundle.getBundle("localisation.en_au"));
            workspaceLoader.setResources(ResourceBundle.getBundle("localisation.en_au"));
        }
        // Just throw an exception for other languages (for now)
        else {
            throw new UnsupportedOperationException("Your language is not yet supported");
        }

        try {
            root.setTop(menuLoader.load());
            root.setLeft(projectLoader.load());
            root.setCenter(workspaceLoader.load());
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }

        Scene scene = new Scene(root, 720, 480);

        // Load our stylesheets
        URL stylesheet = getClass().getClassLoader().getResource("stylesheet.css");

        if (stylesheet != null) {
            scene.getStylesheets().add(stylesheet.toExternalForm());
        }

        primaryStage.setScene(scene);
        primaryStage.setMaximized(true);
        primaryStage.show();
    }

    @Override
    public void stop() {
        for (Map.Entry<UUID, SearchModel> search : GlobalModel.INSTANCE.searchesProperty().entrySet()) {
            search.getValue().stop();
        }
    }
}