package org.iconic.workspace;

import com.google.inject.Inject;
import javafx.beans.InvalidationListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.var;
import lombok.val;
import org.iconic.ea.data.DataManager;
import org.iconic.ea.data.preprocessing.Normalise;
import org.iconic.project.Displayable;
import org.iconic.project.dataset.DatasetModel;
import org.iconic.project.search.SearchModel;
import org.iconic.project.search.SearchService;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
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
    private final WorkspaceService workspaceService;
    private final SearchService searchService;

    @FXML private Button btnSearch;
    @FXML private Button btnStopSearch;
    @FXML private ListView<String> lvFeatures;
    @FXML private LineChart<Number, Number> lcDataView;
    @FXML private CheckBox cbSmoothData;
    @FXML private VBox vbSmoothData;
    @FXML private CheckBox cbHandleMissingValues;
    @FXML private VBox vbHandleMissingValues;
    @FXML private CheckBox cbRemoveOutliers;
    @FXML private VBox vbRemoveOutliers;
    @FXML private CheckBox cbNormalise;
    @FXML private VBox vbNormalise;
    @FXML private TextField tfNormaliseMin;
    @FXML private TextField tfNormaliseMax;


    @Getter(AccessLevel.PRIVATE)
    private final String defaultName;

    @Getter(AccessLevel.PRIVATE)
    private final String defaultWelcomeMessage;

    /**
     * <p>
     * Constructs a new WorkspaceController that attaches an invalidation listener onto the search and workspace
     * services.
     * </p>
     */
    @Inject
    public WorkspaceController(final WorkspaceService workspaceService, final SearchService searchService) {
        this.workspaceService = workspaceService;
        this.searchService = searchService;
        this.defaultName = "";
        this.defaultWelcomeMessage = "Select a dataset on the left to get started.";

        // Update the workspace whenever the active dataset changes
        InvalidationListener selectionChangedListener = observable -> updateWorkspace();
        getWorkspaceService().activeWorkspaceItemProperty().addListener(selectionChangedListener);
        getSearchService().searchesProperty().addListener(selectionChangedListener);
    }

    @Override
    public void initialize(URL arg1, ResourceBundle arg2) {
        updateWorkspace();


        if (lvFeatures != null) {
            // lvFeatures - One of the items in the list is selected and the other objects need to be updates
            lvFeatures.getSelectionModel().selectedItemProperty()
                    .addListener((observable, oldValue, newValue) -> {
                        //System.out.println("You Selected " + newValue);
                        int selectedIndex = lvFeatures.getSelectionModel().getSelectedIndex();
                        featureSelected(selectedIndex);
                    });
        }

        // A quick way to add a listener for the checkboxes
        addListenerToHideElement(cbSmoothData, vbSmoothData);
        addListenerToHideElement(cbHandleMissingValues, vbHandleMissingValues);
        addListenerToHideElement(cbRemoveOutliers, vbRemoveOutliers);
        addListenerToHideElement(cbNormalise, vbNormalise);
    }

    private void addListenerToHideElement(CheckBox cb, VBox vb) {
        if (cb != null)
            cb.selectedProperty().addListener((observable, oldValue, newValue) -> {
                if (vb != null) {
                    vb.setManaged(newValue);
                    vb.setVisible(newValue);
                }
            });
    }

    /**
     * Starts a search using the currently selected dataset.
     *
     * @param actionEvent The action that triggered this event
     */
    public void startSearch(ActionEvent actionEvent) {
        Displayable item = getWorkspaceService().getActiveWorkspaceItem();

        // Check that there's an active dataset before starting the search
        if (item instanceof DatasetModel) {
            val dataset = (DatasetModel) item;
            val search = getSearchService().searchesProperty().get(dataset.getId());

            // If there's no search already being performed on the dataset, start a new one
            if (search == null) {
                SearchModel newSearch = new SearchModel(dataset);
                getSearchService().searchesProperty().put(dataset.getId(), newSearch);

                Thread thread = new Thread(getSearchService().searchesProperty().get(dataset.getId()));
                thread.start();
            }
            // Otherwise stop the current search
            else {
//              TODO implement pause functionality
                search.stop();
                getSearchService().searchesProperty().remove(dataset.getId());
            }
        }
    }

    /**
     * Stops the current search.
     *
     * @param actionEvent The action that triggered this event
     */
    public void stopSearch(ActionEvent actionEvent) {
        Displayable item = getWorkspaceService().getActiveWorkspaceItem();

        // Check that there's an active dataset before starting the search
        if (item instanceof DatasetModel) {
            val dataset = (DatasetModel) item;
            val search = getSearchService().searchesProperty().get(dataset.getId());

            search.stop();
            getSearchService().searchesProperty().remove(dataset.getId());
        }
    }

    /**
     * Updates the lcDataView with new dataset values and toggles all the features that are being
     * used by this feature such as: smoothing, normalised, etc.
     *
     * @param selectedIndex The item index that was selected in the ListView
     */
    public void featureSelected(int selectedIndex) {
        Displayable item = getWorkspaceService().getActiveWorkspaceItem();

        // Update lcDataView
        if (lcDataView != null) {
            //defining a series
            XYChart.Series series = new XYChart.Series();

            //populating the series with data
            lcDataView.getData().clear();

            if (item instanceof DatasetModel) {
                DataManager<Double> dataManager = (DataManager<Double>) getDataManager();
                List<Double> values = (ArrayList<Double>)dataManager.getSampleColumn(selectedIndex);

                for (int sample = 0; sample < values.size(); sample++) {
                    double value = values.get(sample);
                    series.getData().add(new XYChart.Data(sample, value));
                }
            }
            lcDataView.getData().add(series);
        }
    }

    @FXML
    private void normalizeDatasetFeature() {
        if (lvFeatures != null) {
            int selectedIndex = lvFeatures.getSelectionModel().getSelectedIndex();

            if (selectedIndex != -1) {
                DataManager<Double> dataManager = (DataManager<Double>) getDataManager();

                if (cbNormalise.isSelected()) {
                    List<Double> values = dataManager.getSampleColumn(selectedIndex);

                    try {
                        double min = Double.parseDouble(tfNormaliseMin.getText());
                        double max = Double.parseDouble(tfNormaliseMax.getText());

                        if (min < max) {
                            values = Normalise.apply(values, min, max);

                            dataManager.setSampleColumn(selectedIndex, values);
                        }
                    } catch( Exception e) {
                        System.out.println("Min and Max values must be a Number");
                    }
                }
                // Otherwise reset the sample column
                else {
                    getDataManager().resetSampleColumn(selectedIndex);
                }

                featureSelected(selectedIndex);
            }
        }
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

        // Make sure that all the UI elements actually exist
        if (btnSearch != null) {
            // If the selected item is a dataset
            if (item instanceof DatasetModel) {
                val dataset = (DatasetModel) item;
                // Check if a search on the current active dataset is being performed
                val search = getSearchService().searchesProperty().get(dataset.getId());
                btnSearch.setDisable(false);

                // If there's no search...
                if (search != null) {
                    btnSearch.setText("Pause");
                    btnStopSearch.setVisible(true);
                }
                // Otherwise...
                else {
                    btnSearch.setText("Start Search");
                    btnStopSearch.setVisible(false);
                }
            }
            // Otherwise if no interesting project item is selected
            else {
                // Display some default messages
                btnSearch.setText("Start Search");
                btnStopSearch.setVisible(false);
                // And disable the search button
                btnSearch.setDisable(true);
            }
        }

        // Make sure the UI element is actually exists
        if (lvFeatures != null) {
            // If the selected item is a dataset
            if (item instanceof DatasetModel) {
                // Add Dataset Headers to list
                DataManager<Double> dataManager = (DataManager<Double>) getDataManager();

                ObservableList<String> items = FXCollections.observableArrayList (dataManager.getSampleHeaders());
                lvFeatures.setItems(items);
            }
            // Otherwise clear the elements in the table
            else {
                lvFeatures.getItems().clear();
            }
        }

    }

    private void clearUI() {
        // Make sure the UI element is actually exists
        if (lcDataView != null) {
            lcDataView.getData().clear();
        }
    }

    private DataManager<?> getDataManager() {
        Displayable item = getWorkspaceService().getActiveWorkspaceItem();
        DatasetModel dataset = (DatasetModel) item;
        SearchModel search = getSearchService().searchesProperty().get(dataset.getId());

        // If there's no search already being performed on the dataset, start a new one
        if (search == null) {
            search = new SearchModel(dataset);
            getSearchService().searchesProperty().put(dataset.getId(), search);
        }

        return search.getDataManager();
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

    /**
     * <p>
     * Returns the search service of this controller
     * </p>
     *
     * @return the search service of the controller
     */
    private SearchService getSearchService() {
        return searchService;
    }
}