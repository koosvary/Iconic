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

import lombok.extern.log4j.Log4j2;
import org.iconic.ea.chromosome.Chromosome;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.XYSeries;
import org.knowm.xchart.internal.chartpart.Chart;
import org.knowm.xchart.internal.series.Series;
import org.knowm.xchart.style.Styler;
import org.knowm.xchart.style.markers.SeriesMarkers;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@Log4j2
public class XYSeriesWriter extends SeriesWriter<XYSeries> {
    private final List<Number> xValues;
    private final List<Number> yValues;
    private final XYSeries.XYSeriesRenderStyle renderStyle;
    private final Function<Chromosome<?>, Number> xExtractor;
    private final Function<Chromosome<?>, Number> yExtractor;

    public XYSeriesWriter(
            final String seriesName, final XYSeries.XYSeriesRenderStyle renderStyle,
            final Function<Chromosome<?>, Number> xExtractor, final Function<Chromosome<?>, Number> yExtractor
    ) {
        super(seriesName);
        this.xExtractor = xExtractor;
        this.yExtractor = yExtractor;
        this.renderStyle = renderStyle;
        xValues = new ArrayList<>();
        yValues = new ArrayList<>();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void write(final Chromosome<?> chromosome) {
        final Number xValue = getxExtractor().apply(chromosome);
        final Number yValue = getyExtractor().apply(chromosome);
        final Function<Number, Boolean> isValid = v ->
                v.doubleValue() != Double.POSITIVE_INFINITY && v.doubleValue() != Double.NEGATIVE_INFINITY;

        if (isValid.apply(xValue) && isValid.apply(yValue)) {
            getxValues().add(xValue);
            getyValues().add(yValue);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void write(double x, double y) {
        getxValues().add(x);
        getyValues().add(y);
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
        series.setMarker(SeriesMarkers.CROSS);
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
}
