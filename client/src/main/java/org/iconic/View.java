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
package org.iconic;

import com.google.inject.Injector;
import javafx.fxml.FXMLLoader;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;


/**
 * <p>
 * A view loads an FXML resource and  provides it with a dependency graph for constructing its controller.
 * </p>
 */
public class View {
    private final Injector injector;
    private final FXMLLoader loader;

    /**
     * <p>Constructs a new View with the provided FXML resource URI and injector</p>
     *
     * @param uri      The location of the FXML resource
     * @param injector The dependency graph injector
     * @throws UnsupportedOperationException If no localisation resource bundle is available
     */
    public View(final String uri, final Injector injector) throws UnsupportedOperationException {
        final String defaultLocale = "en";
        final String defaultLocaleResource = "localisation.en_au";

        this.injector = injector;
        this.loader = new FXMLLoader(Thread.currentThread().getContextClassLoader().getResource(uri));
        this.loader.setControllerFactory(getInjector()::getInstance);

        // Provide a localisation resource to use for i18n strings
        if (Locale.getDefault().getLanguage().startsWith(defaultLocale)) {
            this.loader.setResources(ResourceBundle.getBundle(defaultLocaleResource));
        }
        // Just throw an exception for other languages (for now)
        else {
            throw new UnsupportedOperationException("Your language is not yet supported");
        }
    }

    /**
     * <p>Loads the FXML resource</p>
     *
     * @param <T> Any type of scene node
     * @return The scene node contained by the FXML resource
     * @throws IOException If the FXML resource cannot be loaded
     */
    public <T> T load() throws IOException {
        return getLoader().load();
    }

    /**
     * <p>Returns the injector for this view</p>
     *
     * @return the injector of the view
     */
    public Injector getInjector() {
        return injector;
    }

    /**
     * <p>Returns the FXML loader for this view</p>
     *
     * @return the FXML loader of the view
     */
    public FXMLLoader getLoader() {
        return loader;
    }
}
