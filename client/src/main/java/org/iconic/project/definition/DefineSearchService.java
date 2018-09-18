package org.iconic.project.definition;

import org.iconic.project.dataset.DatasetModel;
import org.iconic.project.search.io.SearchExecutor;


public interface DefineSearchService {
    /**
     * <p>
     * Returns a list of projects owned by this service.
     * </p>
     *
     * @return The function defined by the user
     */
    String getFunction();

    /**
     * <p>
     * Returns a search model using settings in define search controller.
     * </p>
     *
     * @return the search model as per settings
     */
    SearchExecutor getSearchModel(DatasetModel datasetModel);
}
