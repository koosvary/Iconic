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
package org.iconic.workspace.console;

import com.google.inject.Inject;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.collections.MapChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import org.iconic.control.SearchLogTextArea;
import org.iconic.project.Displayable;
import org.iconic.project.search.config.SearchConfigurationModel;
import org.iconic.project.search.io.SearchExecutor;
import org.iconic.project.search.SearchService;
import org.iconic.workspace.WorkspaceService;

import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * A controller for handling the console and it's output.
 * </p>
 * <p>
 * The ConsoleController maintains a tab pane of console windows for searches to log their results onto.
 * </p>
 * <p>
 * A ConsoleController attaches a change listener onto a search service and it listens for any changes made to the
 * backing search model. When a search is started a corresponding tab is created in the console for the
 * search's output to displayed through, when the search is stopped the tab is removed.
 * </p>
 */
@Log4j2
public class ConsoleController implements Initializable {
    private final WorkspaceService workspaceService;

    @FXML
    private ScrollPane consoleArea;

    /**
     * <p>
     * Constructs a new ConsoleController
     * </p>
     */
    @Inject
    public ConsoleController(
            final WorkspaceService workspaceService
    ) {
        this.workspaceService = workspaceService;

        // Update the console whenever the searches change
        InvalidationListener selectionChangedListener = observable -> updateConsole();
        getWorkspaceService().activeWorkspaceItemProperty().addListener(selectionChangedListener);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Any user interface configuration that needs to happen at construction time must be done in this method to
     * guarantee that it's run after the user interface has been initialised.
     * </p>
     */
    @Override
    public void initialize(URL arg1, ResourceBundle arg2) {
        updateConsole();
    }

    public void updateConsole() {
        Displayable item = getWorkspaceService().getActiveWorkspaceItem();

        // Check the console tab pane as there's no guarantee it will exist when this is triggered
        if (this.consoleArea == null || !(item instanceof SearchConfigurationModel)) {
            return;
        }

        SearchConfigurationModel search = (SearchConfigurationModel) item;

        if (!search.getSearchExecutor().isPresent()) {
            Platform.runLater(() -> consoleArea.setContent(null));
            return;
        }
        search.getSearchExecutor().ifPresent(executor -> {
            TextArea textArea = new SearchLogTextArea(executor);
            // Fit the textarea and scroll pane to the anchor pane
            // *Both* of them need to be set or they'll fall back to the size of their contents
            AnchorPane.setTopAnchor(textArea, 0.0);
            AnchorPane.setLeftAnchor(textArea, 0.0);
            AnchorPane.setRightAnchor(textArea, 0.0);
            AnchorPane.setBottomAnchor(textArea, 0.0);
            consoleArea.setContent(textArea);
            Platform.runLater(() -> consoleArea.setContent(textArea));
        });
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
