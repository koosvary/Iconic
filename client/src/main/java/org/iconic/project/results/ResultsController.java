package org.iconic.project.results;

import com.google.inject.Inject;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.converter.DoubleStringConverter;
import javafx.util.converter.IntegerStringConverter;
import lombok.extern.log4j.Log4j2;
import org.iconic.ea.chromosome.expression.ExpressionChromosome;
import org.iconic.project.Displayable;
import org.iconic.project.dataset.DatasetModel;
import org.iconic.project.search.SearchModel;
import org.iconic.project.search.SearchService;
import org.iconic.project.search.SolutionStorage;
import org.iconic.workspace.WorkspaceService;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * A controller for the Results view
 */
@Log4j2
public class ResultsController implements Initializable {

    private final WorkspaceService workspaceService;
    private final SearchService searchService;

    private SolutionStorage<Double> storage;
    private SearchModel lastSearch;
    private InvalidationListener selectionChangedListener;

    @FXML
    private TableView<ResultDisplay> solutionsTableView;

    /**
     * Constructs a new ResultsController that attaches an invalidation listener onto the workspace service.
     */
    @Inject
    public ResultsController(final WorkspaceService workspaceService, final SearchService searchService) {
        this.workspaceService = workspaceService;
        this.searchService = searchService;

        // Update the workspace whenever the active dataset changes
        selectionChangedListener = observable -> updateWorkspace();
        getWorkspaceService().activeWorkspaceItemProperty().addListener(selectionChangedListener);
        getSearchService().searchesProperty().addListener(selectionChangedListener);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void initialize(URL arg1, ResourceBundle arg2) {
        updateWorkspace();
    }

    /**
     * Updates the workspace to match the current active dataset.
     */
    private synchronized void updateWorkspace() {
        Displayable item = getWorkspaceService().getActiveWorkspaceItem();

        // If no dataset, stop what you're doing.
        if (!(item instanceof DatasetModel)) {
            // TODO clear the UI?
            return;
        }

        DatasetModel dataset = (DatasetModel) item;
        SearchModel search = getSearchModel(dataset);
        if (search != null && search != lastSearch) {
            // If a search is running, use that current one for results. Else use the last search
            storage = search.getSolutionStorage();
            storage.getSolutions().addListener(selectionChangedListener);
            lastSearch = search;
        }

        if (storage == null) {
            // No storage? No worries
            return;
        }

        Platform.runLater(() -> updateWorkspaceMainThread());
    }

    private synchronized void updateWorkspaceMainThread() {
        List<ResultDisplay> resultDisplays = new ArrayList<>();
        for (Map.Entry<Integer, List<ExpressionChromosome<Double>>> entry : storage.getSolutions().entrySet()) {
            ExpressionChromosome<Double> result = entry.getValue().get(0);
            resultDisplays.add(new ResultDisplay(result.getSize(), result.getFitness(), result.toString()));
        }

        // Add all the results as FX observables
        solutionsTableView.setItems(FXCollections.observableArrayList(resultDisplays));

        TableColumn<ResultDisplay, Integer> sizeCol = new TableColumn<>("Size");
        TableColumn<ResultDisplay, Double> fitnessCol = new TableColumn<>("Fitness");
        TableColumn<ResultDisplay, String> solutionCol = new TableColumn<>("Solution");

        // Set conversion factories for data types into string
        sizeCol.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        fitnessCol.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));

        // Set where the values come from
        sizeCol.setCellValueFactory(cellData -> cellData.getValue().sizeProperty().asObject());
        fitnessCol.setCellValueFactory(cellData -> cellData.getValue().fitProperty().asObject());
        solutionCol.setCellValueFactory(cellData -> cellData.getValue().solutionProperty());

        // Set the columns to be these ones
        solutionsTableView.getColumns().setAll(sizeCol, fitnessCol, solutionCol);
    }

    /**
     * Get search model given a dataset
     * @param dataset DatasetModel to use
     * @return Search model for that dataset, or null if no search is running
     */
    private SearchModel getSearchModel(DatasetModel dataset) {
        return getSearchService().searchesProperty().get(dataset.getId());
    }

    /**
     * Get the workspace service
     * @return Workspace service
     */
    private WorkspaceService getWorkspaceService() {
        return workspaceService;
    }

    /**
     * Get the search service
     * @return Search service
     */
    public SearchService getSearchService() {
        return searchService;
    }
}
