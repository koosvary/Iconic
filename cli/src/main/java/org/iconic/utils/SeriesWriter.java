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

import org.iconic.ea.chromosome.Chromosome;
import org.knowm.xchart.VectorGraphicsEncoder;
import org.knowm.xchart.internal.chartpart.Chart;
import org.knowm.xchart.internal.series.Series;
import org.w3c.dom.css.RGBColor;

import java.awt.*;
import java.awt.color.ColorSpace;
import java.io.IOException;
import java.util.Objects;

public abstract class SeriesWriter<T extends Series> {
    final private String seriesName;

    /**
     * Contructs a new SeriesWriter with the specified name.
     *
     * @param seriesName A non-empty name for the series to write.
     */
    SeriesWriter(final String seriesName) {
        Objects.requireNonNull(seriesName, "The name of the series cannot be null.");
        assert (!seriesName.isEmpty());
        this.seriesName = seriesName;
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
     * Resets the plotted values of the series that will be written by the SeriesWriter.
     */
    public abstract void clear();

    /**
     * @return The series of the SeriesWriter.
     */
    public abstract T draw();


    /**
     * @return The name of the series that will be written by the SeriesWriter.
     */
    public String getSeriesName() {
        return seriesName;
    }
}
