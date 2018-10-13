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
 * </p>
 * <p>
 * The MenuController maintains the functionality available through the main menu toolbar.
 * </p>
 */
@Log4j2
public class MenuController implements Initializable {
    private final ProjectService projectService;

    @FXML
    private StackPane pane;

    /**
     * <p>
     * Constructs a new MenuController
     * </p>
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
     * </p>
     */
    @Override
    public void initialize(URL arg1, ResourceBundle arg2) {
        // Do nothing
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
        alert.setHeaderText("Version:    1.0\n\nIcons made by Smashicons, Roundicons, Freepik and DinosoftLabs from www.flaticon.com");

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
     * </p>
     *
     * @return the project service of the controller
     */
    private ProjectService getProjectService() {
        return projectService;
    }
}