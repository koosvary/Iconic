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
package org.iconic.menu;

import com.google.inject.Inject;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.StackPane;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import org.iconic.project.ProjectModel;
import org.iconic.project.ProjectService;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * <p>
 * A controller for handling menu options.
 *
 * <p>
 * The MenuController maintains the functionality available through the main menu toolbar.
 *
 */
@Log4j2
public class MenuController implements Initializable {
    private final ProjectService projectService;

    @FXML
    private StackPane pane;

    /**
     * <p>
     * Constructs a new MenuController
     *
     */
    @Inject
    public MenuController(final ProjectService projectService) {
        this.projectService = projectService;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Any user interface configuration that needs to happen at construction time must be done in this method to
     * guarantee that it's run after the user interface has been initialised.
     *
     */
    @Override
    public void initialize(URL arg1, ResourceBundle arg2) {
        // Do nothing
    }

    /**
     * <p>
     * Closes all stages and exits the application.
     *
     *
     * @param actionEvent The action that triggered this event
     */
    public void exit(ActionEvent actionEvent) {
        Platform.exit();
    }

    /**
     * <p>
     * Returns the root StackPane associated with this view.
     *
     *
     * @return the root StackPane of this controller's view
     */
    private StackPane getPane() {
        return pane;
    }

    /**
     * Shows an about modal
     *
     * @param actionEvent The action that triggered this event
     */
    public void showAbout(ActionEvent actionEvent) {
        val alert = new Alert(
                Alert.AlertType.INFORMATION,
                "Iconic © 2018",
                ButtonType.OK
        );

        alert.setTitle("About");
        alert.setHeaderText("Version: 0.7.0\n\nhttps://github.com/koosvary/Iconic\n\nIcons made by Smashicons, Roundicons, Freepik and DinosoftLabs from www.flaticon.com");

        alert.showAndWait()
                .filter(response -> response == ButtonType.OK)
                .ifPresent(response -> alert.close());
    }

    /**
     * Creates a new project with the name provided by a user's input
     * and then attaches it to the project service
     *
     * @param actionEvent The action that triggered this event
     */
    public void createProject(ActionEvent actionEvent) {
        val defaultName = "";
        val dialog = new TextInputDialog(defaultName);

        dialog.setTitle("New Project");
        dialog.setHeaderText("Project name");

        // Create the project only if a name was provided
        dialog.showAndWait().ifPresent(
                name -> {
                    if (!name.isEmpty()) {
                        final ProjectModel project = ProjectModel.builder().name(name).build();
                        getProjectService().getProjects().add(project);
                    }
                }
        );
    }

    public void loadLightTheme() {
        loadStylesheet("light-theme.css");
    }

    public void loadDarkTheme() {
        loadStylesheet("dark-theme.css");
    }

    public void loadBootstrap2() {
        loadStylesheet("bootstrap2.css");
    }

    public void loadBootStrap3() {
        loadStylesheet("bootstrap3.css");
    }

    /**
     * Loads a new stylesheet with the specified name and removes the other active stylesheet.
     *
     * @param stylesheetName New stylesheet file name
     */
    private void loadStylesheet(String stylesheetName) {
        val stylesheet = getClass().getClassLoader().getResource("css/" + stylesheetName);

        if (stylesheet != null) {
            getPane().getScene().getStylesheets().add(stylesheet.toExternalForm());
            getPane().getScene().getStylesheets().remove(0);
        }
    }

    /**
     * <p>
     * Returns the project service of this controller
     *
     *
     * @return the project service of the controller
     */
    private ProjectService getProjectService() {
        return projectService;
    }
}