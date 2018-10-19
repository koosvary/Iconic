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

import java.io.IOException;

public abstract class GraphWriter {
    /**
     *
     */
    GraphWriter() {
        // Do nothing
    }

    /**
     * Writes the specified chromosome to the chart.
     *
     * @param chromosome The chromosome to write.
     */
    public abstract void write(final Chromosome<?> chromosome);

    /**
     * Writes the specified X-Y value pair to the chart.
     *
     * @param x The x value to write.
     * @param y The y value to write.
     */
    public abstract void write(double x, double y);

    /**
     * @param directory The directory of the exported file.
     * @param fileName  The name of the exported file.
     * @throws IOException Thrown if the file location is inaccessible.
     */
    public void export(final String chartTitle, final String directory, final String fileName) throws IOException {
        getChart().setTitle(chartTitle);
        VectorGraphicsEncoder.saveVectorGraphic(
                getChart(), directory + "//" + fileName,
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
    protected abstract Chart<?, ?> getChart();
}
