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
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.AnchorPane;
import lombok.extern.log4j.Log4j2;
import org.iconic.project.Displayable;
import org.iconic.project.search.config.SearchConfigurationModel;
import org.iconic.workspace.WorkspaceService;

import java.net.URL;
import java.util.*;

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
    private AnchorPane consoleArea;
    @FXML
    private ListView<String> consoleContent;

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
        consoleContent.prefHeightProperty().bind(consoleArea.prefHeightProperty());
        consoleContent.prefWidthProperty().bind(consoleArea.prefWidthProperty());
        consoleContent.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        MenuItem miCopy = new MenuItem("Copy");
        miCopy.setOnAction(this::copyAction);
        miCopy.setAccelerator(KeyCombination.keyCombination("Shortcut+C"));

        ContextMenu menu = new ContextMenu();
        menu.getItems().add(miCopy);
        consoleContent.setContextMenu(menu);
        updateConsole();
    }

    public void updateConsole() {
        Displayable item = getWorkspaceService().getActiveWorkspaceItem();

        // Check the console tab pane as there's no guarantee it will exist when this is triggered
        if (!(item instanceof SearchConfigurationModel)) {
            consoleContent.itemsProperty().unbind();
            return;
        }

        SearchConfigurationModel search = (SearchConfigurationModel) item;

        search.getSearchExecutor().ifPresent(executor -> {
            Platform.runLater(() -> {
                consoleContent.itemsProperty().bind(executor.updatesProperty());
            });
        });
    }

    private void copyAction(ActionEvent actionEvent) {
        final ClipboardContent clipboard = new ClipboardContent();
        final StringBuilder out = new StringBuilder();

        consoleContent.getSelectionModel().getSelectedItems().stream().filter(Objects::nonNull)
                .forEach(item -> out.append(item).append("\n"));

        clipboard.putString(out.toString());
        Clipboard.getSystemClipboard().setContent(clipboard);
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
