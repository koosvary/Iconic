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
