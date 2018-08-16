package org.iconic.project.definition;

import com.google.inject.Inject;
import javafx.beans.InvalidationListener;
import javafx.fxml.Initializable;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

import lombok.NonNull;
import lombok.extern.log4j.Log4j2;

import java.net.URL;
import java.util.List;
import java.util.HashMap;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

import org.iconic.ea.data.DataManager;
import org.iconic.project.Displayable;
import org.iconic.project.ProjectService;
import org.iconic.project.dataset.DatasetModel;
import org.iconic.workspace.WorkspaceService;

@Log4j2
public class DefineSearchController implements Initializable, DefineSearchService {

    private final ProjectService projectService;
    private final WorkspaceService workspaceService;

    private HashMap<String, String> functionDefinitions;

    @FXML
    private TextField tfTargetExpression;

    @Inject
    public DefineSearchController(final ProjectService projectService, final WorkspaceService workspaceService)
    {
        this.projectService = projectService;
        this.workspaceService = workspaceService;
        this.functionDefinitions = new HashMap<String, String>();

        InvalidationListener selectionChangedListener = observable -> loadFunction();
        workspaceService.activeWorkspaceItemProperty().addListener(selectionChangedListener);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {}

    @Override
    public String getFunction()
    {
        String functionStr = null;

        Optional<DataManager<Double>> dataset = getDataManager();

        if(dataset.isPresent()) {
            // Get the ID of the dataset
            String[] splitString = dataset.toString().split("@");
            String datasetID = splitString[splitString.length - 1].replace("]", ""); // There's a trailing ']' from the toString

            // Get the dataset, if exists
            functionStr = functionDefinitions.get(datasetID);
        }

        return functionStr;
    }

    private void loadFunction()
    {
        Optional<DataManager<Double>> dataset = getDataManager();

        if(dataset.isPresent())
        {
            // Get the ID of the dataset
            String[] splitString = dataset.toString().split("@");
            String datasetID = splitString[splitString.length - 1].replace("]", ""); // There's a trailing ']' from the toString

            // No need to redefine the function if one already exists, just insert instead
            String functionStr = functionDefinitions.get(datasetID);
            if(functionStr == null)
            {
                List<String> headers = dataset.get().getSampleHeaders();

                if(!headers.isEmpty())
                {
                    functionStr = generateDefaultFunction(headers);

                    // Save the function defined in the hashmap of all the functions definitions
                    functionDefinitions.put(datasetID, functionStr);
                }
                else
                {
                    log.error("No headers found in this dataset");
                }
            }

            // NOTE(Meyer): Must check if not null otherwise injection will cause an NPE (it's dumb, I know)
            if(tfTargetExpression != null)
            {
                tfTargetExpression.setText(functionStr);
            }
        }
    }

    private String generateDefaultFunction(List<String> headers)
    {
        // Get the last value in arraylist to get the target variable
        String functionResultStr = "(" + headers.get(headers.size() - 1) + ")";
        // Get the first value in list to start the function going
        String functionDefinitionStr = "(" + headers.get(0) + ")";

        // Get all the values bar the first and last column, which we already have
        for(int i = 1; i < headers.size() - 1; i++)
        {
            functionDefinitionStr += ", (" + headers.get(i) + ")";
        }

        return functionResultStr + " = f(" + functionDefinitionStr + ")";
    }

    private Optional<DataManager<Double>> getDataManager() {
        Displayable item = workspaceService.getActiveWorkspaceItem();

        if (item instanceof DatasetModel) {
            DatasetModel dataset = (DatasetModel) item;
            return Optional.of(dataset.getDataManager());
        } else {
            return Optional.empty();
        }
    }
}
;