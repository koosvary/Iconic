/**
 * Copyright 2018 Iconic
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
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

import java.awt.*;
import java.io.IOException;

public abstract class GraphWriter<T extends Series> {
    private boolean axesTruncated;

    /**
     * Constructs a new GraphWriter that will truncate outliers from its axes by default.
     */
    GraphWriter() {
        axesTruncated = true;
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
     * @return The chart of the GraphWriter.
     */
    protected abstract Chart<?, ?> draw();

    /**
     * @return True if outliers will be truncated from the axes of the graph written by the GraphWriter.
     */
    public boolean isAxesTruncated() {
        return axesTruncated;
    }

    /**
     * @param truncate True if the axes of the graph being written should remove outliers.
     */
    public void setAxesTruncated(boolean truncate) {
        this.axesTruncated = truncate;
    }
}
