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

import lombok.extern.log4j.Log4j2;
import org.iconic.ea.chromosome.Chromosome;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.XYSeries;
import org.knowm.xchart.internal.chartpart.Chart;
import org.knowm.xchart.internal.series.Series;
import org.knowm.xchart.style.Styler;
import org.knowm.xchart.style.markers.Marker;
import org.knowm.xchart.style.markers.SeriesMarkers;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@Log4j2
public class XYSeriesWriter extends SeriesWriter<XYSeries> {
    private final List<Number> xValues;
    private final List<Number> yValues;
    private final XYSeries.XYSeriesRenderStyle renderStyle;
    private final Marker marker;
    private final Function<Chromosome<?>, Number> xExtractor;
    private final Function<Chromosome<?>, Number> yExtractor;

    public XYSeriesWriter(
            final String seriesName, final XYSeries.XYSeriesRenderStyle renderStyle,
            final Marker marker
    ) {
        this(seriesName, renderStyle, marker, null, null);
    }

    public XYSeriesWriter(
            final String seriesName, final XYSeries.XYSeriesRenderStyle renderStyle,
            final Marker marker,
            final Function<Chromosome<?>, Number> xExtractor, final Function<Chromosome<?>, Number> yExtractor
    ) {
        super(seriesName);
        this.xExtractor = xExtractor;
        this.yExtractor = yExtractor;
        this.renderStyle = renderStyle;
        this.marker = marker;
        xValues = new ArrayList<>();
        yValues = new ArrayList<>();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void write(final Chromosome<?> chromosome) {
        if (getxExtractor() == null || getyExtractor() == null) {
            log.warn("Attempting to write a chromosome with no extractors");
            return;
        }

        write(
                getxExtractor().apply(chromosome).doubleValue(),
                getyExtractor().apply(chromosome).doubleValue()
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void write(double x, double y) {
        final Function<Double, Boolean> isValid = v ->
                v != Double.POSITIVE_INFINITY && v != Double.NEGATIVE_INFINITY;

        if (isValid.apply(x) && isValid.apply(x)) {
            getxValues().add(x);
            getyValues().add(y);
        }
    }

    /**
     */
    @Override
    public XYSeries draw() {
        Function<List<Number>, double[]> unbox = list ->
            list.stream().mapToDouble(Number::doubleValue).toArray();

        double[] x = unbox.apply(getxValues());
        double[] y = unbox.apply(getyValues());

        XYSeries series =  new XYSeries(
                getSeriesName(), x, y, null, Series.DataType.Number
        );
        series.setXYSeriesRenderStyle(getRenderStyle());
        series.setMarker(getMarker());
        series.setLineColor(series.getLineColor());

        return series;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clear() {
        getxValues().clear();
        getyValues().clear();
    }

    private List<Number> getxValues() {
        return xValues;
    }

    private List<Number> getyValues() {
        return yValues;
    }

    private Function<Chromosome<?>, Number> getxExtractor() {
        return xExtractor;
    }

    private Function<Chromosome<?>, Number> getyExtractor() {
        return yExtractor;
    }

    private XYSeries.XYSeriesRenderStyle getRenderStyle() {
        return renderStyle;
    }

    private Marker getMarker() {
        return marker;
    }
}
