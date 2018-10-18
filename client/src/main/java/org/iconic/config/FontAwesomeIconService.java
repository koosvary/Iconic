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
package org.iconic.config;

import org.controlsfx.glyphfont.Glyph;
import org.controlsfx.glyphfont.GlyphFont;
import org.controlsfx.glyphfont.GlyphFontRegistry;

/**
 * <p>An icon service that provides access to FontAwesome icons
 *
 * @see IconService
 */
public class FontAwesomeIconService implements IconService {
    private final GlyphFont fontRegistery;

    /**
     * {@inheritDoc}
     */
    public FontAwesomeIconService() {
        fontRegistery = GlyphFontRegistry.font("FontAwesome");
    }

    /**
     * <p>Returns the font registry used by this service
     *
     * @return the font registry used by the service
     */
    private GlyphFont getFontRegistry() {
        return fontRegistery;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Glyph getIcon(final Enum<?> glyph) {
        return getFontRegistry().create(glyph);
    }
}
