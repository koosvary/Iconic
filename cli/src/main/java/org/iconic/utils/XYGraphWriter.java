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
import org.iconic.ea.operator.objective.Objective;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.XYSeries;
import org.knowm.xchart.internal.chartpart.Chart;
import org.knowm.xchart.style.Styler;
import org.knowm.xchart.style.markers.Marker;
import org.knowm.xchart.style.markers.SeriesMarkers;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Log4j2
public class XYGraphWriter extends GraphWriter<XYSeries> {
    private final String xAxisTitle;
    private final String yAxisTitle;
    private final List<XYSeries> series;
    private XYChart chart;

    /**
     * Constructs a new XYGraphWriter with the specified axes titles.
     *
     * @param xAxisTitle A non-empty title for the x-axis to write.
     * @param yAxisTitle A non-empty title for the y-axis to write.
     */
    public XYGraphWriter(final String xAxisTitle, final String yAxisTitle) {
        super();
        Objects.requireNonNull(xAxisTitle);
        Objects.requireNonNull(yAxisTitle);
        assert (!xAxisTitle.isEmpty() && !yAxisTitle.isEmpty());

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
        try {
            XYSeries newSeries = getChart().addSeries(series.getName(), series.getXData(), series.getYData());
            newSeries.setXYSeriesRenderStyle(series.getXYSeriesRenderStyle());
            newSeries.setMarker(series.getMarker());
            getSeries().add(newSeries);
        } catch (IllegalArgumentException ex) {
            log.warn("Series was not added because it had no data");
            log.warn("{}: {}", ex::getMessage, ex::getCause);
        }
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
//                series.setLineColor(new Colour1D(j * colours));

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

    /**
     * @return The title of the x-axis.
     */
    private String getxAxisTitle() {
        return xAxisTitle;
    }

    /**
     * @return The title of the y-axis.
     */
    private String getyAxisTitle() {
        return yAxisTitle;
    }

    /**
     * @return All series to be written to the graph.
     */
    private List<XYSeries> getSeries() {
        return series;
    }

    /**
     * Returns the chart of this graph writer. The chart is lazily evaluated and will only be drawn once this method
     * is called. If the writer is modified the graph will not be redrawn.
     *
     * @return The chart of the graph writer.
     */
    private XYChart getChart() {
        if (chart == null) {
            chart = new XYChartBuilder().width(720).height(480)
                    .xAxisTitle(getxAxisTitle()).yAxisTitle(getyAxisTitle())
                    .theme(Styler.ChartTheme.Matlab).build();
            chart.getStyler().setChartTitleVisible(true);
            chart.getStyler().setMarkerSize(12);

            if (isAxesLogarithmic()) {
                chart.getStyler().setXAxisLogarithmic(true);
            }
        }

        return chart;
    }

    private void setChart(final XYChart chart) {
        this.chart = chart;
    }

}
