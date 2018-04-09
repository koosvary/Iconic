package org.aiconic.model;

import org.aiconic.io.DataManager;
import org.aiconic.metaheuristic.Trainer;

import java.util.Arrays;

/**
 * <p>
 * A model for evolutionary searches, it maintains a dataset, data manager, and a trainer.
 * </p>
 * <p>
 * SearchModels implement the Runnable interface so that the search may be performed on a separate thread.
 * </p>
 */
public class SearchModel implements Runnable {
    private final DataManager dataManager;
    private final DatasetModel datasetModel;
    private Trainer trainer;

    /**
     * <p>
     * Constructs a new search model with no dataset, data manager, nor trainer.
     * </p>
     */
    private SearchModel() {
        this.datasetModel = null;
        this.dataManager = null;
        this.trainer = null;
    }

    /**
     * Constructs a new search model with tne provided dataset.
     * @param datasetModel
     *      The dataset to perform the search on
     */
    public SearchModel(final DatasetModel datasetModel) {
        this.datasetModel = datasetModel;
        this.dataManager = new DataManager();
        this.trainer = null;

        try {
            this.trainer = new Trainer();
        } catch (Exception ex) {
            System.err.println(ex.getMessage() + ": ");
            Arrays.stream(ex.getStackTrace()).forEach(System.err::println);
            // TODO: The Trainer throws null pointer exceptions
        }
    }

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
     *      The dataset that this search model is training on
     */
    public DatasetModel getDatasetModel() {
        return datasetModel;
    }
}