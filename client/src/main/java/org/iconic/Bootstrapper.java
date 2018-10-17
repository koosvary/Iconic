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
package org.iconic;

import com.google.inject.Guice;
import com.google.inject.Injector;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import org.iconic.config.InMemoryModule;
import org.iconic.project.ProjectModel;
import org.iconic.project.ProjectService;
import org.iconic.project.search.SearchService;
import org.iconic.project.search.config.SearchConfigurationModel;
import org.iconic.project.search.io.SearchExecutor;
import org.iconic.views.ViewService;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    private final ProjectService projectService;
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
        projectService = injector.getInstance(ProjectService.class);
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
            View cgpConfigView = new View("views/project/search/CgpConfigurationView.fxml", getInjector());
            View gepConfigView = new View("views/project/search/GepConfigurationView.fxml", getInjector());

            getViewService().put("menu", menuView);
            getViewService().put("project-tree", projectView);
            getViewService().put("workspace", workspaceView);
            getViewService().put("cgp-config", cgpConfigView);
            getViewService().put("gep-config", gepConfigView);


            SplitPane splitPane = new SplitPane();
            try {
                splitPane.getItems().add(projectView.load());
                splitPane.getItems().add(workspaceView.load());
                root.setCenter(splitPane);
                root.setTop(menuView.load());
            } catch (IOException ex) {
                log.debug(ex.getMessage());
            }
            Scene scene = new Scene(root, 720, 480);

            // Load our stylesheets
            val stylesheet = getClass().getClassLoader().getResource("css/light-theme.css");

            if (stylesheet != null) {
                scene.getStylesheets().add(stylesheet.toExternalForm());
            }

            primaryStage.setScene(scene);
            primaryStage.setMaximized(true);
            primaryStage.show();
            splitPane.setDividerPositions(0.1);
        } catch (Exception ex) {
            log.error("{}:\n{}", ex::getMessage, ex::getStackTrace);
        }
    }

    @Override
    public void stop() {
        getProjectService().getProjects().stream()
                .flatMap(project -> project.getSearchConfigurations().stream())
                .filter(search -> search.getSearchExecutor().isPresent())
                .map(search -> search.getSearchExecutor().get())
                .forEach(SearchExecutor::stop);
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
     * <p>Returns the project service of this bootstrapper</p>
     *
     * @return the project service of the bootstrapper
     */
    private ProjectService getProjectService() {
        return projectService;
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