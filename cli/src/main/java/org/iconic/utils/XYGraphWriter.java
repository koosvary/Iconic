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
import org.knowm.xchart.style.Styler;
import org.knowm.xchart.style.markers.Marker;
import org.knowm.xchart.style.markers.SeriesMarkers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Log4j2
public class XYGraphWriter extends GraphWriter<XYSeries> {
    private final String xAxisTitle;
    private final String yAxisTitle;
    private final List<XYSeries> series;
    private XYChart chart;

    public XYGraphWriter(final String xAxisTitle, final String yAxisTitle) {
        super();
        this.xAxisTitle = xAxisTitle;
        this.yAxisTitle = yAxisTitle;
        this.series = new ArrayList<>();
        // Create a chart for plotting the final goals
        this.chart = null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void write(final XYSeries series) {
        XYSeries newSeries = getChart().addSeries(series.getName(), series.getXData(), series.getYData());
        newSeries.setXYSeriesRenderStyle(series.getXYSeriesRenderStyle());
        newSeries.setMarker(series.getMarker());
        getSeries().add(newSeries);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clear() {
        getSeries().clear();
        setChart(null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Chart<?, ?> draw() {
        Chart<?, ?> c = getChart();

        final boolean colourSeries = getSeries().size() > 0;

        // If there's more than oen series colour them and hide the series legend (or it will overflow)
        if (colourSeries) {
            final double interval = getSeries().size() / 10.;
            final int colours = 255 * 3 / getSeries().size() + 1;
            boolean setLast = false;

            for (int i = 0; i < getSeries().size(); ++i) {
                final XYSeries series = getSeries().get(i);
                series.setEnabled(false);
            }

            for (int i = 0, j = 1; i < getSeries().size(); ++j) {
                final XYSeries series = getSeries().get(i);
                series.setEnabled(true);
//                series.setLineColor(intToColourSpace(j * colours));

                // If drawing the last series record it
                if (i == getSeries().size() - 1) {
                    setLast = true;
                }

                // If there are more than nine series, increase the interval so only a max of eleven are drawn
                i = (interval >= 0.95)
                        ? (int) Math.floor(interval * j) - 1
                        : i + 1;

                // Always draw the last series
                if (i >= getSeries().size() && !setLast) {
                    i = getSeries().size() - 1;
                }
            }
        }

        // Set the maximum X-axis value to be within two factors of the minimum value
        if (isAxesTruncated()) {
            getSeries().stream()
                    .flatMapToDouble(s -> Arrays.stream(s.getXData()))
                    .min().ifPresent(min -> {
                if (min > 1) {
                    chart.getStyler().setXAxisMax(min * 100.);
                } else {
                    chart.getStyler().setXAxisMax(100.);
                }
            });

            // Set the maximum Y-axis value to the maximum Y value
            getSeries().stream()
                    .flatMapToDouble(s -> Arrays.stream(s.getYData()))
                    .max().ifPresent(max -> chart.getStyler().setYAxisMax(max));

        }

        return c;
    }

    private String getxAxisTitle() {
        return xAxisTitle;
    }

    private String getyAxisTitle() {
        return yAxisTitle;
    }

    private void setChart(final XYChart chart) {
        this.chart = chart;
    }

    private List<XYSeries> getSeries() {
        return series;
    }

    private XYChart getChart() {
        if (chart == null) {
            chart = new XYChartBuilder().width(720).height(480)
                    .xAxisTitle(getxAxisTitle()).yAxisTitle(getyAxisTitle())
                    .theme(Styler.ChartTheme.Matlab).build();
            chart.getStyler().setChartTitleVisible(true);
            chart.getStyler().setMarkerSize(12);
            chart.getStyler().setXAxisLogarithmic(true);
        }

        return chart;
    }
}
