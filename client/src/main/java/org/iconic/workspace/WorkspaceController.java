package org.iconic.workspace;

import com.google.inject.Inject;
import javafx.beans.InvalidationListener;
import javafx.fxml.Initializable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import org.iconic.project.dataset.DatasetModel;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * <p>
 * A controller for handling the workspace view.
 * </p>
 * <p>
 * The WorkspaceController maintains the information shown within the workspace based on the current active dataset.
 * </p>
 */
@Log4j2
public class WorkspaceController implements Initializable {
    private final WorkspaceService workspaceService;

    @Getter(AccessLevel.PRIVATE)
    private final String defaultName;

    @Getter(AccessLevel.PRIVATE)
    private final String defaultWelcomeMessage;

    /**
     * <p>
     * Constructs a new WorkspaceController that attaches an invalidation listener onto the workspace service.
     * </p>
     */
    @Inject
    public WorkspaceController(final WorkspaceService workspaceService) {
        this.workspaceService = workspaceService;
        this.defaultName = "";
        this.defaultWelcomeMessage = "Select a dataset on the left to get started.";

        // Update the workspace whenever the active dataset changes
        InvalidationListener selectionChangedListener = observable -> updateWorkspace();
        getWorkspaceService().activeWorkspaceItemProperty().addListener(selectionChangedListener);
    }

    @Override
    public void initialize(URL arg1, ResourceBundle arg2) {
        updateWorkspace();
    }

    /**
     * Updates the workspace to match the current active dataset.
     */
    private void updateWorkspace() {
        val item = getWorkspaceService().getActiveWorkspaceItem();

        // If no dataset is selected clear the UI
        if (!(item instanceof DatasetModel)) {
            clearUI();
        }
    }

    /**
     * Clears the search graphs.
     */
    private void clearUI() {
        // To add later
    }

    /**
     * <p>
     * Returns the workspace service of this controller
     * </p>
     *
     * @return the workspace service of the controller
     */
    private WorkspaceService getWorkspaceService() {
        return workspaceService;
    }
}