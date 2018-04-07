package org.aiconic;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;

import java.io.IOException;

public abstract class Controller {
    private FXMLLoader loader;

    private Controller() {
        this.loader = null;
    }

    protected Controller(final String fxmlResourcePath) {
        this.loader = new FXMLLoader(getClass().getClassLoader().getResource(fxmlResourcePath));
    }

    public Node getView() throws IOException {
        return getLoader().load();
    }

    protected FXMLLoader getLoader() {
        if (this.loader == null) {
            throw new MissingFxmlLoaderException("Components must instantiate their loaders before use");
        }

        return this.loader;
    }

    private class MissingFxmlLoaderException extends RuntimeException {
        public MissingFxmlLoaderException(final String message) {
            super(message);
        }
    }
}