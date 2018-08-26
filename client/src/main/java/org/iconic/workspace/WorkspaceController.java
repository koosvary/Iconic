package org.iconic.workspace;

import com.google.inject.Inject;
import javafx.beans.InvalidationListener;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import org.iconic.ea.data.DataManager;
import org.iconic.ea.data.preprocessing.TransformType;
import org.iconic.ea.data.preprocessing.Transformation;
import org.iconic.project.Displayable;
import org.iconic.project.dataset.DatasetModel;
import org.iconic.project.definition.DefineSearchService;
import org.iconic.project.search.SearchModel;
import org.iconic.project.search.SearchService;
import org.iconic.ea.data.preprocessing.Normalise;
import org.iconic.ea.data.preprocessing.Smooth;

import java.awt.*;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.*;
import java.util.ArrayList;

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
    private final SearchService searchService;
    private final WorkspaceService workspaceService;
    private final DefineSearchService defineSearchService;
    // A flag which determines whether the pre-processing checkboxes are disabled by the user
    // or reset within this class. It's used to avoid firing the changeListener's changed()
    // event when manually enabling/disabling checkboxes.
    private boolean resetCheckboxFlag = false;

    @FXML
    private Button btnSearch;
    @FXML
    private Button btnStopSearch;
    @FXML
    private ListView<String> lvFeatures;
    @FXML
    private LineChart<Number, Number> lcDataView;
    @FXML
    private LineChart<Number, Number> lcSearchProgress;
    @FXML
    private CheckBox cbSmoothData;
    @FXML
    private VBox vbSmoothData;
    @FXML
    private CheckBox cbHandleMissingValues;
    @FXML
    private VBox vbHandleMissingValues;
    @FXML
    private CheckBox cbRemoveOutliers;
    @FXML
    private VBox vbRemoveOutliers;
    @FXML
    private CheckBox cbNormalise;
    @FXML
    private VBox vbNormalise;
    @FXML
    private CheckBox cbFilter;
    @FXML
    private VBox vbFilter;
    @FXML
    private TextField tfNormaliseMin;
    @FXML
    private TextField tfNormaliseMax;
    @FXML
    private TextField tfTargetExpression;


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
    public WorkspaceController(final WorkspaceService workspaceService, final SearchService searchService, final DefineSearchService defineSearchService) {
        this.defineSearchService = defineSearchService;
        this.searchService = searchService;
        this.workspaceService = workspaceService;
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
            lvFeatures.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                        // Once a feature is selected, the pre-processing checkboxes are enabled
                        enablePreprocessingCheckBoxes();

                        // Update the data view and pre-processing text fields
                        int selectedIndex = lvFeatures.getSelectionModel().getSelectedIndex();
                        featureSelected(selectedIndex);

                        // Update the respective checkboxes of the selected feature
                        updateCheckBoxes(selectedIndex);
            });
        }

        // A quick way to add a listener for the checkboxes
        addChangeListener(cbSmoothData, vbSmoothData);
        addChangeListener(cbHandleMissingValues, vbHandleMissingValues);
        addChangeListener(cbRemoveOutliers, vbRemoveOutliers);
        addChangeListener(cbNormalise, vbNormalise);
        addChangeListener(cbFilter, vbFilter);
    }

    /**
     * Adds a ChangeListener to the pre-processing checkboxes to fire the changed() event when the user
     * selects or deselects an option.
     *
     * @param checkbox
     * @param vbox
     */
    private void addChangeListener(CheckBox checkbox, VBox vbox) {
        if (checkbox != null) {
            checkbox.selectedProperty().addListener(new ChangeListener<Boolean>() {
                @Override
                public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                    if (!resetCheckboxFlag) {
                        if (newValue) {
                            // Checkbox has been selected
                            System.out.println("Checkbox ticked.");
                        } else {
                            // Checkbox has been unselected
                            System.out.println("Checkbox unticked");

                            removeExistingTransform(checkbox);
                        }
                    }

                    // Shows or hides the checkboxes options based on whether it is selected or not
                    if (vbox != null) {
                        vbox.setManaged(newValue);
                        vbox.setVisible(newValue);
                    }
                }
            });
        }
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
            // TODO(Meyer): Use the function defined to determine what data is used, and what to calculate to
            log.info("Function for use: " + defineSearchService.getFunction());

            DatasetModel dataset = (DatasetModel) item;
            SearchModel search = getSearchService().searchesProperty().get(dataset.getId());

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
                stopSearch(actionEvent);
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

            lcSearchProgress.getData().clear();
            lcSearchProgress.getData().add(search.getPlots());

            search.stop();
            getSearchService().searchesProperty().remove(dataset.getId());
        }
    }

    /**
     * Updates the lcDataView with new dataset values and the pre-processing checkboxes text
     * fields to reflect the currently selected feature.
     *
     * @param selectedIndex The item index that was selected in the ListView
     */
    private void featureSelected(int selectedIndex) {
        // Update lcDataView
        if (lcDataView != null) {
            // Stores the currently selected header in the lvFeatures list
            String selectedHeader = "";

            // defining a series
            XYChart.Series<Number, Number> series = new XYChart.Series<>();

            // populating the series with data
            lcDataView.getData().clear();
            Optional<DataManager<Double>> dataManager = getDataManager();

            if (dataManager.isPresent() && selectedIndex >= 0) {
                ArrayList<Number> values = dataManager.get().getSampleColumn(selectedIndex);

                for (int sample = 0; sample < values.size(); sample++) {
                    double value = values.get(sample).doubleValue();
                    series.getData().add(new XYChart.Data<>(sample, value));
                }
            }
            lcDataView.getData().add(series);

            // Updates the preprocessing methods text fields to reflect the respective header
            if (dataManager.isPresent()) {
                selectedHeader = dataManager.get().getSampleHeaders().get(selectedIndex);
                updatePreprocessingTextFields(selectedHeader);
            }
        }
    }

    /**
     * Applies a normalisation transform to the current dataset stored in the DataManager.
     */
    public void normalizeDatasetFeature() {
        if (lvFeatures != null) {
            int selectedIndex = lvFeatures.getSelectionModel().getSelectedIndex();

            if (selectedIndex != -1) {
                Optional<DataManager<Double>> dataManager = getDataManager();

                if (cbNormalise.isSelected() && dataManager.isPresent()) {
                    ArrayList<Number> values = dataManager.get().getSampleColumn(selectedIndex);

                    try {
                        double min = Double.parseDouble(tfNormaliseMin.getText());
                        double max = Double.parseDouble(tfNormaliseMax.getText());

                        if (min < max) {
                            values = Normalise.apply(values, min, max);
                            dataManager.get().setSampleColumn(selectedIndex, values);
                        }
                    } catch (Exception e) {
                        log.error("Min and Max values must be a Number");
                    }

                    // Retrieves the header string of the currently selected feature
                    String selectedHeader = dataManager.get().getSampleHeaders().get(selectedIndex);

                    addNewTransform(selectedHeader, TransformType.Normalised);
                }
                // Otherwise reset the sample column
                else if (dataManager.isPresent()) {
                    dataManager.get().resetSampleColumn(selectedIndex);
                }

                featureSelected(selectedIndex);
            }
        }
    }

    /**
     * Applies a smoothing transform to the current dataset stored in the DataManager.
     */
    public void smoothDatasetFeature() {
        if (lvFeatures != null) {
            int selectedIndex = lvFeatures.getSelectionModel().getSelectedIndex();

            if (selectedIndex != -1) {
                Optional<DataManager<Double>> dataManager = getDataManager();

                if (cbSmoothData.isSelected() && dataManager.isPresent()) {
                    ArrayList<Number> values = dataManager.get().getSampleColumn(selectedIndex);

                    values = Smooth.apply(values);
                    dataManager.get().setSampleColumn(selectedIndex, values);

                    // Retrieves the header string of the currently selected feature
                    String selectedHeader = dataManager.get().getSampleHeaders().get(selectedIndex);

                    addNewTransform(selectedHeader, TransformType.Smoothed);
                }
                // Otherwise reset the sample column
                else if (dataManager.isPresent()) {
                    dataManager.get().resetSampleColumn(selectedIndex);
                }

                featureSelected(selectedIndex);
            }
        }
    }

    private void addNewTransform(String header, TransformType transformType) {
        Optional<DataManager<Double>> dataManager = getDataManager();

        // Creates a new transformation object, storing the feature that was transformed and
        // the type of transformation
        Transformation newTransformation = new Transformation(header, transformType);

        if (dataManager.isPresent()) {
            dataManager.get().getDataset().get(header).addTransformation(newTransformation);
            dataManager.get().getDataset().get(header).setModified(true);
        }
    }

    private void removeExistingTransform(CheckBox checkbox) {
        Optional<DataManager<Double>> dataManager = getDataManager();

        if (dataManager.isPresent()) {
            String selectedHeader = dataManager.get().getSampleHeaders().get(lvFeatures.getSelectionModel().getSelectedIndex());
            TransformType transformType = convertCheckBoxToTransformType(checkbox);

            dataManager.get().getDataset().get(selectedHeader).removeTransformation(transformType);

            if (dataManager.get().getDataset().get(selectedHeader).getTransformations().size() == 0) {
                dataManager.get().getDataset().get(selectedHeader).setModified(false);
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
        if (btnSearch != null && btnStopSearch != null) {
            // If the selected item is a dataset
            if (item instanceof DatasetModel) {
                DatasetModel dataset = (DatasetModel) item;
                // Check if a search on the current active dataset is being performed
                SearchModel search = getSearchService().searchesProperty().get(dataset.getId());

                // If there's no search...
                if (search == null) {
                    btnSearch.setText("Start Search");
                    btnSearch.setDisable(false);
                    btnStopSearch.setDisable(true);
                    btnStopSearch.setVisible(false);
                }
                // Otherwise...
                else {
                    btnSearch.setText("Pause");
                    btnSearch.setDisable(true);
                    btnStopSearch.setDisable(false);
                    btnStopSearch.setVisible(true);
                }
            }
            // Otherwise if no interesting project item is selected
            else {
                // Display some default messages
                btnSearch.setText("Start Search");
                btnStopSearch.setVisible(false);
                // And disable the search buttons
                btnSearch.setDisable(true);
                btnStopSearch.setDisable(true);
            }
        }

        //  Make sure the UI element actually exist
        if (lvFeatures != null) {
            // If the selected item is a dataset
            if (item instanceof DatasetModel) {
                // Add Dataset Headers to list
                Optional<DataManager<Double>> dataManager = getDataManager();

                if (dataManager.isPresent()) {
                    ObservableList<String> items = FXCollections.observableArrayList(dataManager.get().getSampleHeaders());
                    lvFeatures.setItems(items);
                }
            }
            // Otherwise clear the elements in the table
            else {
                lvFeatures.getItems().clear();
            }
        }
    }

    /**
     * Updates the pre-processing checkboxes to reflect the currently selected header.
     * This method is called each time a new feature is selected in lvFeatures.
     *
     * @param selectedHeader The string value of the currently selected header
     */
    private void updatePreprocessingTextFields(String selectedHeader) {
        // Updates the selected header in the transformation text fields
        cbSmoothData.setText("Smooth data points of (" + selectedHeader + ")");
        cbHandleMissingValues.setText("Handle missing values of (" + selectedHeader + ")");
        cbRemoveOutliers.setText("Remove outliers of (" + selectedHeader + ")");
        cbNormalise.setText("Normalise scale and offset of (" + selectedHeader + ")");
        cbFilter.setText("Filter data of (" + selectedHeader + ")");
    }

    /**
     * Updates the pre-processing checkboxes to be either selected (ticked) or not selected (unticked)
     * based upon a list of applied transformations.
     * This method is called each time a new feature is selected in lvFeatures.
     *
     * @param selectedIndex The integer corresponding to the currently selected feature
     */
    private void updateCheckBoxes(int selectedIndex) {
        clearCheckBoxes();

        Optional<DataManager<Double>> dataManager = getDataManager();

        if (dataManager.isPresent()) {
            String selectedHeader = dataManager.get().getSampleHeaders().get(selectedIndex);

            if (dataManager.get().getDataset().get(selectedHeader).isModified()) {
                List<Transformation> transformations = dataManager.get().getDataset().get(selectedHeader).getTransformations();

                for (int i=0; i < transformations.size(); i++) {
                    enableCheckBox(transformations.get(i).getTransform());
                }
            }
        }
    }

    /**
     * Takes a transformation type as input and selects the checkbox corresponding with that
     * transformation type.
     *
     * @param transform The type of a previous transform
     */
    private void enableCheckBox(TransformType transform) {
        resetCheckboxFlag = true;

        switch (transform) {
            case Smoothed:
                cbSmoothData.setSelected(true);
                break;

            case MissingValuesHandled:
                cbHandleMissingValues.setSelected(true);
                break;

            case OutliersRemoved:
                cbRemoveOutliers.setSelected(true);
                break;

            case Normalised:
                cbNormalise.setSelected(true);
                break;

            case Filtered:
                cbFilter.setSelected(true);
                break;
        }

        resetCheckboxFlag = false;
    }

    /**
     * Enabled all pre-processing checkboxes so that the user can interact with them.
     * This method is called once a feature is selected.
     */
    private void enablePreprocessingCheckBoxes() {
        cbSmoothData.setDisable(false);
        cbHandleMissingValues.setDisable(false);
        cbRemoveOutliers.setDisable(false);
        cbNormalise.setDisable(false);
        cbFilter.setDisable(false);
    }

    /**
     * Given one of the five pre-processing checkboxes, this method determines its corresponding TransformType.
     *
     * @param checkbox The checkbox that has been selected
     * @return The corresponding TransformType
     */
    private TransformType convertCheckBoxToTransformType(CheckBox checkbox) {
        if (checkbox == cbSmoothData) {
            return TransformType.Smoothed;
        }
        else if (checkbox == cbHandleMissingValues) {
            return TransformType.MissingValuesHandled;
        }
        else if (checkbox == cbRemoveOutliers) {
            return TransformType.OutliersRemoved;
        }
        else if (checkbox == cbNormalise) {
            return TransformType.Normalised;
        }
        else {
            return TransformType.Filtered;
        }
    }

    /**
     * Resets all checkboxes to an unselected state.
     * This method is called before checkboxes are updated to reflect previous transformations.
     */
    private void clearCheckBoxes() {
        resetCheckboxFlag = true;

        cbSmoothData.setSelected(false);
        cbHandleMissingValues.setSelected(false);
        cbRemoveOutliers.setSelected(false);
        cbNormalise.setSelected(false);
        cbFilter.setSelected(false);

        resetCheckboxFlag = false;
    }

    /**
     * Clears the search graphs.
     */
    private void clearUI() {
        // Make sure the UI element actually exists
        if (lcDataView != null) {
            lcDataView.getData().clear();
        }

        if (lcSearchProgress != null) {
            lcSearchProgress.getData().clear();
        }
    }

    private Optional<DataManager<Double>> getDataManager() {
        Displayable item = getWorkspaceService().getActiveWorkspaceItem();

        if (item instanceof DatasetModel) {
            DatasetModel dataset = (DatasetModel) item;
            return Optional.of(dataset.getDataManager());
        } else {
            return Optional.empty();
        }
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