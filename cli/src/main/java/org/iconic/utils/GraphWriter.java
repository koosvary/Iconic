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

import java.awt.*;
import java.io.IOException;

public abstract class GraphWriter<T extends Series> {
    /**
     *
     */
    GraphWriter() {
        // Do nothing
    }

    /**
     * Writes the specified series to the chart.
     *
     * @param series The series to write.
     */
    public abstract void write(final T series);

    /**
     * @param directory The directory of the exported file.
     * @param fileName  The name of the exported file.
     * @throws IOException Thrown if the file location is inaccessible.
     */
    public void export(final String chartTitle, final String directory, final String fileName) throws IOException {
        Chart<?, ?> chart = draw();
        chart.setTitle(chartTitle);
        VectorGraphicsEncoder.saveVectorGraphic(
                chart, directory + "//" + fileName,
                VectorGraphicsEncoder.VectorGraphicsFormat.PDF
        );
    }

    /**
     * Resets the plotted values of the chart.
     */
    public abstract void clear();

    /**
     * Returns the chart of this GraphWriter.
     *
     * @return The chart of the GraphWriter.
     */
    protected abstract Chart<?, ?> draw();

    protected Color intToColourSpace(final int point) {
        final int b = (point > 255 * 2) ? point % 256 : 0;
        final int g = (point > 255) ? point % 256 : 0;
        final int r = point % 256;
        return new Color(r, g, b);
    }
}
