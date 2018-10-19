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
import org.knowm.xchart.style.Styler;
import org.knowm.xchart.style.markers.SeriesMarkers;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@Log4j2
public class XYGraphWriter extends GraphWriter {
    private final List<Number> xValues;
    private final List<Number> yValues;
    private final String xAxisTitle;
    private final String yAxisTitle;
    private final String seriesName;
    private final XYSeries.XYSeriesRenderStyle renderStyle;
    private final Function<Chromosome<?>, Number> xExtractor;
    private final Function<Chromosome<?>, Number> yExtractor;
    private XYChart chart;

    public XYGraphWriter(
            final String xAxisTitle, final String yAxisTitle,
            final String seriesName, final XYSeries.XYSeriesRenderStyle renderStyle,
            final Function<Chromosome<?>, Number> xExtractor, final Function<Chromosome<?>, Number> yExtractor
    ) {
        super();
        this.xAxisTitle = xAxisTitle;
        this.yAxisTitle = yAxisTitle;
        this.seriesName = seriesName;
        this.xExtractor = xExtractor;
        this.yExtractor = yExtractor;
        this.renderStyle = renderStyle;
        // Create a chart for plotting the final goals
        chart = null;
        xValues = new ArrayList<>();
        yValues = new ArrayList<>();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void write(final Chromosome<?> chromosome) {
            getxValues().add(getxExtractor().apply(chromosome));
            getyValues().add(getyExtractor().apply(chromosome));
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
    private void draw() {
        XYSeries series = ((XYChart) getChart()).addSeries(getSeriesName(), getxValues(), getyValues());
        series.setMarker(SeriesMarkers.CROSS);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clear() {
        getxValues().clear();
        getyValues().clear();
        setChart(null);
    }

    /**
     *
     * @return
     */
    @Override
    protected Chart<?, ?> getChart() {
        if (chart == null) {
            chart = new XYChartBuilder().width(720).height(480)
                    .xAxisTitle(getxAxisTitle()).yAxisTitle(getyAxisTitle())
                    .theme(Styler.ChartTheme.GGPlot2).build();
            chart.getStyler().setDefaultSeriesRenderStyle(getRenderStyle());
            chart.getStyler().setChartTitleVisible(true);
            chart.getStyler().setLegendPosition(Styler.LegendPosition.OutsideS);
            chart.getStyler().setMarkerSize(16);
            ((XYChart)getChart()).getStyler().setXAxisLogarithmic(true);
            draw();
        }
        return chart;
    }

    private String getxAxisTitle() {
        return xAxisTitle;
    }

    private String getyAxisTitle() {
        return yAxisTitle;
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

    private String getSeriesName() {
        return seriesName;
    }

    private void setChart(final XYChart chart) {
        this.chart = chart;
    }
}
