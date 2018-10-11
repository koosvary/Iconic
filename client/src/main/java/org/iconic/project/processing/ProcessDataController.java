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
package org.iconic.project.processing;

import com.google.inject.Inject;
import javafx.beans.InvalidationListener;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import org.iconic.ea.data.DataManager;
import org.iconic.ea.data.preprocessing.*;
import org.iconic.project.Displayable;
import org.iconic.project.dataset.DatasetModel;
import org.iconic.workspace.WorkspaceService;

import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * A controller class for handling the ProcessData view.
 *
 * The ProcessDataController provides a bridge between applying pre-processing options from the GUI and updating
 * the modified data within the DataManager.
 */
@Log4j2
public class ProcessDataController implements Initializable {
    private final WorkspaceService workspaceService;

    // A flag which determines whether the pre-processing checkboxes are disabled by the user
    // or reset within this class. It's used to avoid firing the changeListener's changed()
    // event when manually enabling/disabling checkboxes.
    private boolean resetCheckboxFlag = false;

    @FXML
    private ListView<String> lvFeatures;
    @FXML
    private LineChart<Number, Number> lcDataView;
    @FXML
    private CheckBox cbSmoothData, cbHandleMissingValues, cbRemoveOutliers, cbNormalise, cbOffset;
    @FXML
    private VBox vbSmoothData, vbHandleMissingValues, vbRemoveOutliers, vbNormalise, vbOffset;
    @FXML
    private ComboBox<String> cbHandleMissingValuesOptions;
    @FXML
    private TextField tfNormaliseMin, tfNormaliseMax, tfOffsetValue;
    @FXML
    private Spinner<Double> spRemoveOutliersThreshold;

    /**
     * <p>
     * Constructs a new ProcessDataController that attaches an invalidation listener onto the workspace service.
     * </p>
     */
    @Inject
    public ProcessDataController(final WorkspaceService workspaceService) {
        this.workspaceService = workspaceService;

        // Update the workspace whenever the active dataset changes
        InvalidationListener selectionChangedListener = observable -> updateWorkspace();
        getWorkspaceService().activeWorkspaceItemProperty().addListener(selectionChangedListener);
    }

    /**
     * Initialize is called each time an item is selected in lvFeatures. The checkboxes are updated based
     * on previously applied preprocessors. A change listener is also attached to each checkbox/combobox.
     *
     * @param arg1
     * @param arg2
     */
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
        addCheckBoxChangeListener(cbSmoothData, vbSmoothData);
        addCheckBoxChangeListener(cbHandleMissingValues, vbHandleMissingValues);
        addCheckBoxChangeListener(cbRemoveOutliers, vbRemoveOutliers);
        addCheckBoxChangeListener(cbNormalise, vbNormalise);
        addCheckBoxChangeListener(cbOffset, vbOffset);

        addComboBoxChangeListener(cbHandleMissingValuesOptions, cbHandleMissingValues);

        initializeSpinner();
        addSpinnerChangeListener(spRemoveOutliersThreshold, cbRemoveOutliers);
    }

    /**
     * Creates a ValueFactory for the spinner with the specified parameters, and also formats the spinners values to
     * 2 decimal places.
     */
    private void initializeSpinner() {
        SpinnerValueFactory.DoubleSpinnerValueFactory valueFactory = new SpinnerValueFactory.DoubleSpinnerValueFactory(0, 99.99, 2.00, 0.10);

        valueFactory.setConverter(new StringConverter<Double>() {
            @Override
            public String toString(Double value) {
                return String.format("%.2f", value);
            }

            @Override
            public Double fromString(String string) {
                return Double.parseDouble(string);
            }
        });

        spRemoveOutliersThreshold.setValueFactory(valueFactory);
    }

    /**
     * Adds a ChangeListener to the pre-processing checkboxes to fire the changed() event when the user
     * selects or deselects an option.
     *
     * @param checkbox The selected checkbox
     * @param vbox The pre-processing parameters to be enabled when a checkbox is selected
     */
    private void addCheckBoxChangeListener(CheckBox checkbox, VBox vbox) {
        if (checkbox == null) {
            return;
        }

        checkbox.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (!resetCheckboxFlag) {
                    Optional<DataManager<Double>> dataManager = getDataManager();

                    if (dataManager.isPresent()) {
                        int selectedIndex = lvFeatures.getSelectionModel().getSelectedIndex();
                        String selectedHeader = dataManager.get().getSampleHeaders().get(selectedIndex);

                        if (newValue) {
                            // Checkbox has been selected - call the checkboxes corresponding function
                            convertTransformTypeToFunction(convertCheckBoxToTransformType(checkbox));
                        } else {
                            // Checkbox has been unselected - remove the checkboxes corresponding preprocessor
                            removeExistingPreprocessor(checkbox);
                        }

                        updateModifiedText(selectedIndex, selectedHeader);
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

    /**
     * Adds a ChangeListener to the handle missing values combo box in order to detect when a user
     * selects a new option. It then performs the handle missing values pre-processing.
     *
     * @param combobox The selected combo box
     * @param checkbox The selected check box to identify the transformType
     */
    private void addComboBoxChangeListener(ComboBox combobox, CheckBox checkbox) {
        if (combobox == null) {
            return;
        }

        cbHandleMissingValuesOptions.getSelectionModel().selectFirst();
        combobox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                Optional<DataManager<Double>> dataManager = getDataManager();

                if (dataManager.isPresent()) {
                    removeExistingPreprocessor(checkbox);

                    int selectedIndex = lvFeatures.getSelectionModel().getSelectedIndex();
                    String selectedHeader = dataManager.get().getSampleHeaders().get(selectedIndex);

                    convertTransformTypeToFunction(convertCheckBoxToTransformType(checkbox));

                    updateModifiedText(selectedIndex, selectedHeader);
                }
            }
        });
    }

    private void addSpinnerChangeListener(Spinner<Double> spinner, CheckBox checkbox) {
        if (spinner == null) {
            return;
        }

        spinner.valueProperty().addListener(new ChangeListener<Double>() {
            @Override
            public void changed(ObservableValue<? extends Double> observable, Double oldValue, Double newValue) {
                Optional<DataManager<Double>> dataManager = getDataManager();

                if (dataManager.isPresent()) {
                    removeExistingPreprocessor(checkbox);

                    int selectedIndex = lvFeatures.getSelectionModel().getSelectedIndex();
                    String selectedHeader = dataManager.get().getSampleHeaders().get(selectedIndex);

                    convertTransformTypeToFunction(convertCheckBoxToTransformType(checkbox));

                    updateModifiedText(selectedIndex, selectedHeader);
                }
            }
        });
    }

    /**
     * Called after a checkbox has been selected. A check is done to determine whether the feature
     * has been modified. If so, a 'modified' keyword is appended to the header and the feature list
     * is updated.
     *
     * @param selectedIndex Index of currently selected feature
     * @param selectedHeader Header of currently selected feature
     */
    private void updateModifiedText(int selectedIndex, String selectedHeader) {
        Optional<DataManager<Double>> dataManager = getDataManager();
        ObservableList items = lvFeatures.getItems();

        if (dataManager.isPresent()) {
            String newHeader = dataManager.get().getSampleHeaders().get(selectedIndex);

            if (dataManager.get().getDataset().get(selectedHeader).isModified()) {
                newHeader += "    modified";
            }

            items.set(selectedIndex, newHeader);

            lvFeatures.setItems(items);
        }
    }

    /**
     * Updates the lcDataView with new dataset values and the pre-processing checkboxes text
     * fields to reflect the currently selected feature.
     *
     * @param selectedIndex The item index that was selected in the ListView
     */
    private void featureSelected(int selectedIndex) {
        // We're updating lcDataView
        if (lcDataView == null) {
            return;
        }

        // Defining a series
        XYChart.Series<Number, Number> series = new XYChart.Series<>();

        // Populating the series with data
        lcDataView.getData().clear();
        Optional<DataManager<Double>> dataManager = getDataManager();

        if (dataManager.isPresent() && selectedIndex >= 0) {
            List<Number> values = dataManager.get().getSampleColumn(selectedIndex);

            for (int sample = 0; sample < values.size(); sample++) {
                double value = values.get(sample).doubleValue();
                series.getData().add(new XYChart.Data<>(sample, value));
            }
        }
        lcDataView.getData().add(series);

        // Updates the pre-processing methods text fields to reflect the respective header
        if (dataManager.isPresent() && selectedIndex >= 0) {
            String selectedHeader = dataManager.get().getSampleHeaders().get(selectedIndex);
            updatePreprocessingTextFields(selectedHeader);
        }
    }

    /**
     * Creates a Smooth object which is added to the list of currently active preprocessors.
     *
     */
    public void smoothDatasetFeature() {
        if (lvFeatures == null) {
            return;
        }

        int selectedIndex = lvFeatures.getSelectionModel().getSelectedIndex();
        if (selectedIndex != -1) {
            Optional<DataManager<Double>> dataManager = getDataManager();

            if (cbSmoothData.isSelected() && dataManager.isPresent()) {
                Smooth smooth = new Smooth();
                smooth.setTransformType(TransformType.Smoothed);

                addNewPreprocessor(dataManager.get().getSampleHeaders().get(selectedIndex), smooth);
            }

            featureSelected(selectedIndex);
        }
    }

    /**
     * Creates a HandleMissingValues object which is added to the list of currently active preprocessors.
     *
     */
    public void handleMissingValuesOfDatasetFeature() {
        if (lvFeatures == null) {
            return;
        }

        int selectedIndex = lvFeatures.getSelectionModel().getSelectedIndex();
        if (selectedIndex != -1) {
            Optional<DataManager<Double>> dataManager = getDataManager();

            if (cbHandleMissingValues.isSelected() && dataManager.isPresent()) {
                HandleMissingValues handleMissingValues = new HandleMissingValues();
                handleMissingValues.setTransformType(TransformType.MissingValuesHandled);
                handleMissingValues.setMode(convertComboBoxIndexToMode(cbHandleMissingValuesOptions.getSelectionModel().getSelectedIndex()));

                addNewPreprocessor(dataManager.get().getSampleHeaders().get(selectedIndex), handleMissingValues);
            }

            featureSelected(selectedIndex);
        }
    }

    /**
     * Creates a RemoveOutliers object which is added to the list of currently active preprocessors.
     */
    public void removeOutliersInDatasetFeature() {
        if (lvFeatures == null) {
            return;
        }

        int selectedIndex = lvFeatures.getSelectionModel().getSelectedIndex();
        if (selectedIndex != -1) {
            Optional<DataManager<Double>> dataManager = getDataManager();

            if (cbRemoveOutliers.isSelected() && dataManager.isPresent()) {
                double threshold = spRemoveOutliersThreshold.getValue();

                RemoveOutliers removeOutliers = new RemoveOutliers();
                removeOutliers.setTransformType(TransformType.OutliersRemoved);
                removeOutliers.setThreshold(threshold);

                addNewPreprocessor(dataManager.get().getSampleHeaders().get(selectedIndex), removeOutliers);
            }

            featureSelected(selectedIndex);
        }
    }

    /**
     * Creates a Normalise object which is added to the list of currently active preprocessors.
     *
     */
    public void normalizeDatasetFeature() {
        if (lvFeatures == null) {
            return;
        }

        int selectedIndex = lvFeatures.getSelectionModel().getSelectedIndex();
        if (selectedIndex != -1) {
            Optional<DataManager<Double>> dataManager = getDataManager();

            if (cbNormalise.isSelected() && dataManager.isPresent()) {
                try {
                    double min = Double.parseDouble(tfNormaliseMin.getText());
                    double max = Double.parseDouble(tfNormaliseMax.getText());

                    if (min < max) {
                        Normalise normalise = new Normalise(min, max);
                        normalise.setTransformType(TransformType.Normalised);

                        addNewPreprocessor(dataManager.get().getSampleHeaders().get(selectedIndex), normalise);
                    }
                } catch (Exception e) {
                    log.error("Min and Max values must be a Number");
                }
            }

            featureSelected(selectedIndex);
        }
    }

    /**
     * Creates an Offset object which is added to the list of currently active preprocessors.
     *
     */
    public void offsetDatasetFeature() {
        if (lvFeatures == null) {
            return;
        }

        int selectedIndex = lvFeatures.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0) {
            Optional<DataManager<Double>> dataManager = getDataManager();

            if (cbOffset.isSelected() && dataManager.isPresent()) {
                try {
                    double offsetValue = Double.parseDouble(tfOffsetValue.getText());

                    Offset offset = new Offset(offsetValue);
                    offset.setTransformType(TransformType.Offset);

                    addNewPreprocessor(dataManager.get().getSampleHeaders().get(selectedIndex), offset);

                } catch (Exception e) {
                    log.error("Offset value must be a Number");
                }
            }

            featureSelected(selectedIndex);
        }
    }

    /**
     * Adds a new preprocessor to the FeatureClass list
     *
     * @param header Used to identify the specific FeatureClass which the preprocessor is being applied to
     * @param preprocessor The new preprocessor object
     */
    private void addNewPreprocessor(String header, Preprocessor preprocessor) {
        Optional<DataManager<Double>> dataManager = getDataManager();

        if (dataManager.isPresent()) {
            dataManager.get().getDataset().get(header).addPreprocessor(preprocessor);
        }
    }

    /**
     * Removes an existing preprocessor after a pre-processing checkbox has been deselected
     *
     * @param checkbox Identifies the type of transform to be removed
     */
    private void removeExistingPreprocessor(CheckBox checkbox) {
        Optional<DataManager<Double>> dataManager = getDataManager();

        if (dataManager.isPresent()) {
            String selectedHeader = dataManager.get().getSampleHeaders().get(lvFeatures.getSelectionModel().getSelectedIndex());
            TransformType transformType = convertCheckBoxToTransformType(checkbox);

            // Remove selected preprocessor and reapply the other active ones
            dataManager.get().getDataset().get(selectedHeader).removePreprocessor(transformType);
        }

        featureSelected(lvFeatures.getSelectionModel().getSelectedIndex());
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
        cbNormalise.setText("Normalise scale of (" + selectedHeader + ")");
        cbOffset.setText("Offset values of (" + selectedHeader + ")");
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

        if (dataManager.isPresent() && selectedIndex >= 0) {
            String selectedHeader = dataManager.get().getSampleHeaders().get(selectedIndex);

            if (dataManager.get().getDataset().get(selectedHeader).isModified()) {
                List<Preprocessor<Number>> preprocessors = dataManager.get().getDataset().get(selectedHeader).getPreprocessors();

                for (Preprocessor preprocessor : preprocessors) {
                    enableCheckBox(preprocessor.getTransformType());
                }
            }
        }
    }

    /**
     * Takes a TransformType as input and selects the checkbox corresponding with that
     * transformation type.
     *
     * @param transformType The type of a previous transform
     */
    private void enableCheckBox(TransformType transformType) {
        resetCheckboxFlag = true;

        switch (transformType) {
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

            case Offset:
                cbOffset.setSelected(true);
                break;
        }

        resetCheckboxFlag = false;
    }

    /**
     * Enables all pre-processing checkboxes so that the user can interact with them.
     * This method is called once a feature is selected.
     */
    private void enablePreprocessingCheckBoxes() {
        cbSmoothData.setDisable(false);
        cbHandleMissingValues.setDisable(false);
        cbRemoveOutliers.setDisable(false);
        cbNormalise.setDisable(false);
        cbOffset.setDisable(false);
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
            return TransformType.Offset;
        }
    }

    /**
     * Given one of transform types, this method determines its corresponding function.
     *
     * @param transformType
     */
    private void convertTransformTypeToFunction(TransformType transformType) {
        switch (transformType) {
            case Smoothed:
                smoothDatasetFeature();
                break;

            case MissingValuesHandled:
                handleMissingValuesOfDatasetFeature();
                break;

            case OutliersRemoved:
                removeOutliersInDatasetFeature();
                break;

            case Normalised:
                normalizeDatasetFeature();
                break;

            case Offset:
                offsetDatasetFeature();
                break;
        }
    }

    /**
     *  Converts a handle missing values combo box index into the respective HandleMissingValues.Mode type.
     *
     * @param index Identifies the currently selected option in the combo box.
     */
    private HandleMissingValues.Mode convertComboBoxIndexToMode(int index) {
        switch (index) {
            /* Can be re-added once copyPreviousRow is fixed
            case 0:
                return Mode.COPY_PREVIOUS_ROW;*/

            case 0:
                return HandleMissingValues.Mode.MEAN;

            case 1:
                return HandleMissingValues.Mode.MEDIAN;

            case 2:
                return HandleMissingValues.Mode.ZERO;

            case 3:
            default:
                return HandleMissingValues.Mode.ONE;
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
        cbOffset.setSelected(false);

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
    }

    /**
     * Returns the DataManager object which stores original and modified datasets.
     */
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
}
