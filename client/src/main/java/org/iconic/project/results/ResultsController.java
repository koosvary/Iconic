package org.iconic.project.results;

import com.google.inject.Inject;
import javafx.beans.InvalidationListener;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableView;
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
        InvalidationListener selectionChangedListener = observable -> updateWorkspace();
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
    private void updateWorkspace() {
        Displayable item = getWorkspaceService().getActiveWorkspaceItem();

        // If no dataset is selected clear the UI
        if (!(item instanceof DatasetModel)) {
            clearUI();
            return;
        }

        DatasetModel dataset = (DatasetModel) item;
        SearchModel search = getSearchModel(dataset);
        if (search == null) {
            return;
        }
        SolutionStorage<Double> storage = search.getSolutionStorage();

        List<ResultDisplay> resultDisplays = new ArrayList<>();
        for (Map.Entry<Integer, List<ExpressionChromosome<Double>>> entry : storage.getSolutions().entrySet()) {
            ExpressionChromosome<Double> result = entry.getValue().get(0);
            resultDisplays.add(new ResultDisplay(result.getSize(), result.getFitness(), result.toString()));
        }

        solutionsTableView.setItems(FXCollections.observableArrayList());
    }

    /**
     * Clears the search graphs.
     */
    private void clearUI() {
        // TODO add later
    }

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