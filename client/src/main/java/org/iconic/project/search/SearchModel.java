package org.iconic.project.search;

import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import org.iconic.io.DataManager;
import org.iconic.metaheuristic.Trainer;
import org.iconic.project.dataset.DatasetModel;

import java.util.Arrays;

/**
 * <p>
 * A global for evolutionary searches, it maintains a dataset, data manager, and a trainer.
 * </p>
 * <p>
 * SearchModels implement the Runnable interface so that the search may be performed on a separate thread.
 * </p>
 */
@Log4j2
public class SearchModel implements Runnable {
    private final DataManager dataManager;
    private final DatasetModel datasetModel;
    private Trainer trainer;

    /**
     * Constructs a new search global with tne provided dataset.
     * @param datasetModel
     *      The dataset to perform the search on
     */
    public SearchModel(@NonNull final DatasetModel datasetModel) {
        this.datasetModel = datasetModel;
        this.dataManager = new DataManager();
        this.trainer = null;

        try {
            this.trainer = new Trainer();
        } catch (Exception ex) {
            log.debug(ex.getMessage());
            Arrays.stream(ex.getStackTrace()).forEach(log::debug);
            // TODO: The Trainer throws null pointer exceptions
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void run() {
        dataManager.importData(datasetModel.getAbsolutePath());
        dataManager.normalizeScale();

        if (trainer != null) {
            try {
                trainer.startSearch();
            } catch (Exception ex) {
                System.err.println(ex.getMessage() + ": ");
                Arrays.stream(ex.getStackTrace()).forEach(System.out::println);
                // TODO: The Trainer throws null pointer exceptions
            }
        }
    }

    /**
     * <p>
     * Stops any ongoing search.
     * </p>
     */
    public void stop() {
        if (trainer != null) {
            trainer.stopSearch();
        }
    }

    /**
     * <p>
     * Returns the dataset that's being trained on.
     * </p>
     * @return
     *      The dataset that this search global is training on
     */
    public DatasetModel getDatasetModel() {
        return datasetModel;
    }
}