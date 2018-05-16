package org.iconic.control;

import javafx.scene.control.TextArea;
import org.iconic.project.search.SearchModel;

public class SearchLogTextArea extends TextArea {
    private final SearchModel model;

    public SearchLogTextArea(final SearchModel model) {
        this.model = model;
        this.setWrapText(false);
        this.textProperty().bind(model.updatesProperty());
    }

    public SearchModel getModel() {
        return model;
    }
}
