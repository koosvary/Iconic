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
package org.iconic.config;

import org.controlsfx.glyphfont.Glyph;
import org.controlsfx.glyphfont.GlyphFont;
import org.controlsfx.glyphfont.GlyphFontRegistry;

/**
 * <p>An icon service that provides access to FontAwesome icons</p>
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
     * <p>Returns the font registry used by this service</p>
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
