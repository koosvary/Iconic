package org.iconic;

import com.google.inject.Guice;
import com.google.inject.Injector;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import org.iconic.config.InMemoryModule;
import org.iconic.project.search.SearchService;
import org.iconic.views.ViewService;

import java.io.IOException;

/**
 * {@inheritDoc}
 * <p>
 * The bootstrapper defines the entry point for the graphical user interface and performs all the necessary setup
 * to link views to their controllers and models.
 * </p>
 */
@Log4j2
public class Bootstrapper extends Application {
    private final Injector injector;
    private final SearchService searchService;
    private final ViewService viewService;

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
     * <p>Constructs a new Bootstrapper</p>
     */
    public Bootstrapper() {
        super();
        injector = Guice.createInjector(new InMemoryModule());
        searchService = injector.getInstance(SearchService.class);
        viewService = injector.getInstance(ViewService.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void start(Stage primaryStage) {
        try {

            // Create the primary stage
        primaryStage.setTitle("- Iconic Workbench");

        // Create the root node for placing all the other components
        val root = new BorderPane();

        // Load the child UI elements from FXML resources
        View menuView = new View("views/menu/MenuView.fxml", getInjector());
        View projectView = new View("views/project/ProjectTreeView.fxml", getInjector());
        View workspaceView = new View("views/workspace/WorkspaceView.fxml", getInjector());

        getViewService().put("menu", menuView);
        getViewService().put("project-tree", projectView);
        getViewService().put("workspace", workspaceView);

        try {
            root.setTop(menuView.load());
            root.setLeft(projectView.load());
            root.setCenter(workspaceView.load());
        } catch (IOException ex) {
            log.debug(ex.getMessage());
        }

        Scene scene = new Scene(root, 720, 480);

        // Load our stylesheets
        val stylesheet = getClass().getClassLoader().getResource("stylesheet.css");

        if (stylesheet != null) {
            scene.getStylesheets().add(stylesheet.toExternalForm());
        }

            primaryStage.setScene(scene);
            primaryStage.setMaximized(true);
            primaryStage.show();
        }
        catch (Exception ex) {
            log.error("{}:\n{}", ex::getMessage, ex::getStackTrace);
        }
    }

    @Override
    public void stop() {
        for (val search : getSearchService().searchesProperty().entrySet()) {
            search.getValue().stop();
        }
    }

    /**
     * <p>Returns the default injector of this bootstrapper</p>
     *
     * @return the default injector of the bootstrapper
     */
    private Injector getInjector() {
        return injector;
    }

    /**
     * <p>Returns the search service of this bootstrapper</p>
     *
     * @return the search service of the bootstrapper
     */
    private SearchService getSearchService() {
        return searchService;
    }

    /**
     * <p>Returns the view service of this bootstrapper</p>
     *
     * @return the view service of the bootstrapper
     */
    public ViewService getViewService() {
        return viewService;
    }
}