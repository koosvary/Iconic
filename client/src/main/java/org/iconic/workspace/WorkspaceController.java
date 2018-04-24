package org.iconic.workspace;

import javafx.beans.InvalidationListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.val;
import org.iconic.project.dataset.DatasetModel;
import org.iconic.global.GlobalModel;
import org.iconic.project.search.SearchModel;
import org.iconic.project.Displayable;

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
public class WorkspaceController implements Initializable {
    @FXML
    private Button btnSearch;

    @FXML
    private Text txtName;

    @FXML
    private Text txtWelcome;

    @Getter(AccessLevel.PRIVATE)
    private final String defaultName;

    @Getter(AccessLevel.PRIVATE)
    private final String defaultWelcomeMessage;

    /**
     * <p>
     * Constructs a new WorkspaceController that attaches an invalidation listener onto the global global.
     * </p>
     */
    public WorkspaceController() {
        this.defaultName = "";
        this.defaultWelcomeMessage = "Select a dataset on the left to get started.";

        // Update the workspace whenever the active dataset changes
        InvalidationListener selectionChangedListener = observable -> updateWorkspace();
        GlobalModel.INSTANCE.activeProjectItemProperty().addListener(selectionChangedListener);
        GlobalModel.INSTANCE.searchesProperty().addListener(selectionChangedListener);
    }

    @Override
    public void initialize(URL arg1, ResourceBundle arg2) {
        updateWorkspace();
    }

    /**
     * Starts a search using the currently selected dataset.
     *
     * @param actionEvent
     *      The action that triggered this event
     */
    public void startSearch(ActionEvent actionEvent) {
        Displayable item = GlobalModel.INSTANCE.getActiveProjectItem();

        // Check that there's an active dataset before starting the search
        if (item instanceof DatasetModel) {
            val dataset = (DatasetModel) item;
            val search = GlobalModel.INSTANCE.searchesProperty().get(dataset.getId());

            // If there's no search already being performed on the dataset, start a new one
            if (search == null) {
                SearchModel newSearch = new SearchModel(dataset);
                GlobalModel.INSTANCE.searchesProperty().put(dataset.getId(), newSearch);

                Thread thread = new Thread(GlobalModel.INSTANCE.searchesProperty().get(dataset.getId()));
                thread.start();
            }
            // Otherwise stop the current search
            else {
                search.stop();
                GlobalModel.INSTANCE.searchesProperty().remove(dataset.getId());
            }
        }
    }

    /**
     * Updates the workspace to match the current active dataset.
     */
    public void updateWorkspace() {
        val item = GlobalModel.INSTANCE.getActiveProjectItem();

        // Make sure that all the UI elements actually exist
        if (btnSearch != null && txtName != null && txtWelcome != null) {
            // If the selected item is a dataset
            if (item instanceof DatasetModel) {
                val dataset = (DatasetModel) item;
                // Check if a search on the current active dataset is being performed
                val search = GlobalModel.INSTANCE.searchesProperty().get(dataset.getId());
                txtName.setText(dataset.getLabel());
                txtWelcome.setText("Selected dataset: ");
                btnSearch.setDisable(false);

                // If there's no search...
                if (search != null) {
                    btnSearch.setText("Stop Search");
                }
                // Otherwise...
                else {
                    btnSearch.setText("Start Search");
                }
            }
            // Otherwise if no interesting project item is selected
            else {
                // Display some default messages
                txtName.setText(getDefaultName());
                txtWelcome.setText(getDefaultWelcomeMessage());
                btnSearch.setText("Start Search");
                // And disable the search button
                btnSearch.setDisable(true);
            }

        }
    }


}