package org.iconic.workspace;

import com.google.inject.Inject;
import javafx.beans.InvalidationListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import org.iconic.control.WorkspaceTab;
import org.iconic.ea.data.DataManager;
import org.iconic.ea.data.preprocessing.Normalise;
import org.iconic.ea.data.preprocessing.Smooth;
import org.iconic.project.Displayable;
import org.iconic.project.dataset.DatasetModel;
import org.iconic.project.definition.DefineSearchService;
import org.iconic.project.search.SearchConfigurationModel;
import org.iconic.project.search.SearchExecutor;
import org.iconic.project.search.SearchService;

import java.net.URL;
import java.util.ArrayList;
import java.util.Optional;
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
    private final SearchService searchService;
    private final WorkspaceService workspaceService;
    private final DefineSearchService defineSearchService;

    @FXML
    private TabPane tbpWorkspace;
    @FXML
    private Tab tbInputDataView;
    @FXML
    private Tab tbProcessDataView;
    @FXML
    private Tab tbDefineSearchView;
    @FXML
    private Tab tbStartSearchView;
    @FXML
    private Tab tbResultsView;
    @FXML
    private Tab tbReportView;
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
            lvFeatures.getSelectionModel().selectedItemProperty()
                    .addListener((observable, oldValue, newValue) -> {
                        int selectedIndex = lvFeatures.getSelectionModel().getSelectedIndex();
                        featureSelected(selectedIndex);
                    });
        }

        // A quick way to add a listener for the checkboxes
        addListenerToHideElement(cbSmoothData, vbSmoothData);
        addListenerToHideElement(cbHandleMissingValues, vbHandleMissingValues);
        addListenerToHideElement(cbRemoveOutliers, vbRemoveOutliers);
        addListenerToHideElement(cbNormalise, vbNormalise);
        addListenerToHideElement(cbFilter, vbFilter);
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
            // TODO(Meyer): Use the function defined to determine what data is used, and what to calculate to
            log.info("Function for use: " + defineSearchService.getFunction());

            DatasetModel dataset = (DatasetModel) item;
            SearchExecutor search = getSearchService().searchesProperty().get(dataset.getId());

            // If there's no search already being performed on the dataset, start a new one
            if (search == null) {
                SearchExecutor newSearch = new SearchExecutor(dataset);
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
     * Updates the lcDataView with new dataset values and toggles all the features that are being
     * used by this feature such as: smoothing, normalised, etc.
     *
     * @param selectedIndex The item index that was selected in the ListView
     */
    public void featureSelected(int selectedIndex) {
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

                selectedHeader = String.valueOf(dataManager.get().getSampleHeaders().get(selectedIndex));
            }
            lcDataView.getData().add(series);

            // Updates the selected header in the transformation text fields
            cbSmoothData.setText("Smooth data points of (" + selectedHeader + ")");
            cbHandleMissingValues.setText("Handle missing values of (" + selectedHeader + ")");
            cbRemoveOutliers.setText("Remove outliers of (" + selectedHeader + ")");
            cbNormalise.setText("Normalise scale and offset of (" + selectedHeader + ")");
            cbFilter.setText("Filter data of (" + selectedHeader + ")");
        }
    }

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
                }
                // Otherwise reset the sample column
                else {
                    dataManager.ifPresent(dm -> dm.resetSampleColumn(selectedIndex));
                }

                featureSelected(selectedIndex);
            }
        }
    }

    public void smoothDatasetFeature() {
        if (lvFeatures != null) {
            int selectedIndex = lvFeatures.getSelectionModel().getSelectedIndex();

            if (selectedIndex != -1) {
                Optional<DataManager<Double>> dataManager = getDataManager();

                if (cbSmoothData.isSelected() && dataManager.isPresent()) {
                    ArrayList<Number> values = dataManager.get().getSampleColumn(selectedIndex);

                    values = Smooth.apply(values);
                    dataManager.get().setSampleColumn(selectedIndex, values);
                }
                // Otherwise reset the sample column
                else {
                    dataManager.ifPresent(dm -> dm.resetSampleColumn(selectedIndex));
                }

                featureSelected(selectedIndex);
            }
        }
    }

    /**
     * <p>Update the workspace to match the current active item</p>
     */
    private void updateWorkspace() {
        val item = getWorkspaceService().getActiveWorkspaceItem();

        // If no dataset is selected clear the UI of dataset related elements
        // TODO: if a search is selected, do not clear the results
        if (!(item instanceof DatasetModel)) {
            clearUI();
        }

        if (item instanceof DatasetModel) {
            updateAvailableTabs(WorkspaceTab.TabType.DATASET);
        } else if (item instanceof SearchConfigurationModel) {
            updateAvailableTabs(WorkspaceTab.TabType.SEARCH);
        } else {
//            updateAvailableTabs(WorkspaceTab.TabType.OTHER);
        }
        // Otherwise a dataset is selected, so we need to ensure that any non-dataset related UI elements
        // are disabled, and vice-versa

        // Make sure that all the UI elements actually exist
        if (btnSearch != null && btnStopSearch != null) {
            // If the selected item is a dataset
            if (item instanceof DatasetModel) {
                DatasetModel dataset = (DatasetModel) item;
                // Check if a search on the current active dataset is being performed
                SearchExecutor search = getSearchService().searchesProperty().get(dataset.getId());

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
     * <p>Update the tabs that are available for selection based on the provided tab type</p>
     *
     * @param availableTabs The tab types to enable, tabs of other types will be disabled
     */
    private void updateAvailableTabs(WorkspaceTab.TabType availableTabs) {
        if (tbpWorkspace != null) {
            tbpWorkspace.getTabs().forEach(tab -> {
                WorkspaceTab wTab = (WorkspaceTab) tab;
                if (wTab.getTabType().equals(availableTabs)) {
                    tab.setDisable(false);
                } else {
                    tab.setDisable(true);
                }
            });

            // If the current selected tab is disabled, change the user's
            // selection to the first enabled tab if available
            updateTabSelection();
        }
    }

    /**
     * <p>Update the tab selection model if the user has a disabled tab selected</p>
     *
     * <p>The selection model will default to the first enabled tab if available, otherwise no tab will be selected</p>
     */
    private void updateTabSelection() {
        SingleSelectionModel<Tab> selectionModel = tbpWorkspace.getSelectionModel();
        Tab selectedTab = selectionModel.getSelectedItem();

        if (selectedTab.isDisable()) {
            // Clear the user's selection if their selected tab is disabled
            selectionModel.clearSelection();
            FilteredList<Tab> filteredTabs = tbpWorkspace.getTabs().filtered(Tab::isDisable);

            if (filteredTabs.size() > 0) {
                Tab newSelection = filteredTabs.get(0);
                tbpWorkspace.getSelectionModel().select(newSelection);
            }
        }
    }

    /**
     * <p>Clear all context-specific UI data</p>
     */
    private void clearUI() {
        // Reset the data view chart
        if (lcDataView != null) {
            lcDataView.getData().clear();
        }

        // Reset the search process chart
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