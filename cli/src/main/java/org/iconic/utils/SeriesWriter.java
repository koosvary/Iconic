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
package org.iconic.utils;

import org.iconic.ea.chromosome.Chromosome;
import org.knowm.xchart.VectorGraphicsEncoder;
import org.knowm.xchart.internal.chartpart.Chart;
import org.knowm.xchart.internal.series.Series;
import org.w3c.dom.css.RGBColor;

import java.awt.*;
import java.awt.color.ColorSpace;
import java.io.IOException;

public abstract class SeriesWriter<T extends Series> {
    final private String seriesName;

    /**
     *
     */
    SeriesWriter(final String seriesName) {
        this.seriesName = seriesName;
        // Do nothing
    }

    /**
     * Writes the specified chromosome to the series.
     *
     * @param chromosome The chromosome to write, none of its extracted values may be positive or negative infinity.
     */
    public abstract void write(final Chromosome<?> chromosome);

    /**
     * Writes the specified X-Y value pair to the series.
     *
     * @param x Mustn't be positive or negative infinity.
     * @param y Mustn't be positive or negative infinity.
     */
    public abstract void write(double x, double y);


    /**
     * Resets the plotted values of the series.
     */
    public abstract void clear();

    /**
     * @return The series of the SeriesWriter.
     */
    public abstract T draw();

    public String getSeriesName() {
        return seriesName;
    }
}
