package org.aiconic.workspace;

import javafx.beans.InvalidationListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import org.aiconic.model.DatasetModel;
import org.aiconic.model.GlobalModel;
import org.aiconic.model.SearchModel;

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

    private final String defaultName;
    private final String defaultWelcomeMessage;

    /**
     * <p>
     * Constructs a new WorkspaceController that attaches an invalidation listener onto the global model.
     * </p>
     */
    public WorkspaceController() {
        this.defaultName = "";
        this.defaultWelcomeMessage = "Select a dataset on the left to get started.";

        // Update the workspace whenever the active dataset changes
        InvalidationListener selectionChangedListener = observable -> updateWorkspace();
        GlobalModel.INSTANCE.activeDatasetProperty().addListener(selectionChangedListener);
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
        DatasetModel dataset = GlobalModel.INSTANCE.getActiveDataset();

        // Check that there's an active dataset before starting the search
        if (dataset != null) {
            final SearchModel search = GlobalModel.INSTANCE.searchesProperty().get(dataset.getId());

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
        final DatasetModel dataset = GlobalModel.INSTANCE.getActiveDataset();

        // Make sure that all the UI elements actually exist
        if (btnSearch != null && txtName != null && txtWelcome != null) {
            // If no dataset is selected
            if (dataset == null) {
                // Display some default messages
                txtName.setText(defaultName);
                txtWelcome.setText(defaultWelcomeMessage);
                btnSearch.setText("Start Search");
                // And disable the search button
                btnSearch.setDisable(true);
            }
            // Otherwise
            else {
                // Check if a search on the current active dataset is being performed
                final SearchModel search = GlobalModel.INSTANCE.searchesProperty().get(dataset.getId());
                txtName.setText(dataset.getName());
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
        }
    }


}