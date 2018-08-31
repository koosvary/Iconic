package org.iconic.config;

import org.controlsfx.glyphfont.Glyph;

/**
 * <p>An icon service provides users with access to icons</p>
 *
 * <p>Implementers of this interface can retrieve icons either locally or from a web-based resource,
 * as such it's important to ensure that any icon service used is instantiated as soon as possible.</p>
 */
public interface IconService {
    /**
     * <p>Returns an icon based on the provided glyph identifier</p>
     *
     * @param glyph The glyph identifier to iconify
     * @return an icon glyph
     */
    Glyph getIcon(final Enum<?> glyph);
}
