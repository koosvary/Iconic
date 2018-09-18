package org.iconic.control;

import javafx.scene.control.TextArea;
import org.iconic.project.search.io.SearchExecutor;

public class SearchLogTextArea extends TextArea {
    private final SearchExecutor model;

    public SearchLogTextArea(final SearchExecutor model) {
        this.model = model;
        this.setWrapText(false);
        this.textProperty().bind(model.updatesProperty());
    }

    public SearchExecutor getModel() {
        return model;
    }
}
