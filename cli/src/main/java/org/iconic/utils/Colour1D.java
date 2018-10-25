package org.iconic.utils;

import java.awt.*;

/**
 * A helper class for converting a one-dimensional scalar value into a three-dimensional RGB colour vector.
 *
 * @see java.awt.Color
 */
public class Colour1D {
    private final Color colour;
    private final int point;

    /**
     * Constructs a new Colour1D at the specified point.
     *
     * @param point A value less than zero or greater than 765 will always be black or white.
     */
    public Colour1D(final int point) {
        this.point = point;
        final int r = (toPoint() >= 255) ? 255 : toPoint();
        final int g = (toPoint() >= 255 * 2) ? 255 : (toPoint() <= 255) ? 0 : toPoint() % 255;
        final int b = (toPoint() >= 255 * 3) ? 255 : (toPoint() <= 255 * 2) ? 0 : toPoint() % (255 * 2);
        this.colour = new Color(r, g, b);
    }

    /**
     * @return A Color
     * @see java.awt.Color
     */
    public static Color toColour(final int point) {
        return new Colour1D(point).toColour();
    }

    /**
     * @return A Color
     * @see java.awt.Color
     */
    public Color toColour() {
        return colour;
    }

    /**
     * @return A point
     */
    public int toPoint() {
        return point;
    }
}
