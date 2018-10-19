/**
 * Copyright 2018 Iconic
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.iconic;

import com.google.inject.Injector;
import javafx.fxml.FXMLLoader;

import java.io.IOException;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;


/**
 * <p>
 * A view loads an FXML resource and  provides it with a dependency graph for constructing its controller.
 *
 */
public class View {
    private final Optional<Injector> injector;
    private final FXMLLoader loader;

    /**
     * <p>Constructs a new View with the provided FXML resource URI and injector
     *
     * @param uri      The location of the FXML resource
     * @param injector The dependency graph injector
     * @throws UnsupportedOperationException If no localisation resource bundle is available
     */
    public View(final String uri, final Injector injector) throws UnsupportedOperationException {
        final String defaultLocale = "en";
        final String defaultLocaleResource = "localisation.en_au";

        this.injector = (injector != null) ? Optional.of(injector) : Optional.empty();
        this.loader = new FXMLLoader(Thread.currentThread().getContextClassLoader().getResource(uri));

        this.injector.ifPresent(i ->
            this.loader.setControllerFactory(i::getInstance)
        );

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
     * <p>Loads the FXML resource
     *
     * @param <T> Any type of scene node
     * @return The scene node contained by the FXML resource
     * @throws IOException If the FXML resource cannot be loaded
     */
    public <T> T load() throws IOException {
        return getLoader().load();
    }

    /**
     * <p>Returns the injector for this view
     *
     * @return the injector of the view
     */
    public Optional<Injector> getInjector() {
        return injector;
    }

    /**
     * <p>Returns the FXML loader for this view
     *
     * @return the FXML loader of the view
     */
    public FXMLLoader getLoader() {
        return loader;
    }
}
