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
        alert.setHeaderText("About");

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
                    final ProjectModel project = ProjectModel.builder().name(name).build();
                    getProjectService().getProjects().add(project);
                }
        );
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