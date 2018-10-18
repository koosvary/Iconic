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

            try {
                root.setTop(menuView.load());
                root.setLeft(projectView.load());
                root.setCenter(workspaceView.load());
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