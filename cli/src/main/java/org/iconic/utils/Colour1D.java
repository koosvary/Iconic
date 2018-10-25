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
