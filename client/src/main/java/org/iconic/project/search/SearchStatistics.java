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
package org.iconic.project.search;

import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import lombok.extern.log4j.Log4j2;
import java.util.concurrent.locks.Lock;

import org.iconic.project.Displayable;
import org.iconic.project.search.config.SearchConfigurationModel;
import org.iconic.project.search.io.SearchExecutor;
import org.iconic.workspace.WorkspaceService;

/**
 * Worker service to update the GUI on the Start Search screen
 */
@Log4j2
public class SearchStatistics extends Service<Void> {

    private static final int START_PAUSE = 500;
    private static final int SLEEP_TIME = 250;

    private final WorkspaceService workspaceService;
    private StartSearchController controller;
    private SearchExecutor<?> search;
    private Lock updating;

    /**
     * Worker service to update the GUI on the Start Search screen
     * @param workspaceService The workspace service
     * @param controller The controller
     * @param search Current search
     * @param updating Lock for updating
     */
    public SearchStatistics(
            WorkspaceService workspaceService,
            StartSearchController controller,
            SearchExecutor<?> search,
            Lock updating
    ) {
        this.workspaceService = workspaceService;
        this.controller = controller;
        this.search = search;
        this.updating = updating;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Task<Void> createTask() {
        return new Task<Void>() {
            @Override
            protected Void call() {
                try {
                    Thread.sleep(START_PAUSE);
                    while (search.isRunning()) {
                        Displayable item = getWorkspaceService().getActiveWorkspaceItem();

                        if (!(item instanceof SearchConfigurationModel)) {
                            Thread.sleep(SLEEP_TIME);
                            continue;
                        }

                        SearchConfigurationModel activeSearch = (SearchConfigurationModel) item;

                        if (!activeSearch.getSearchExecutor().isPresent()) {
                            Thread.sleep(SLEEP_TIME);
                            continue;
                        }
                        if (activeSearch.getSearchExecutor().get() == search) {
                            Platform.runLater(() -> {
                                controller.getTxtTime().setText(getTimeElapsed());
                                controller.getTxtGen().setText(search.getGeneration() + "");
                                controller.getTxtGenSec().setText(
                                        String.format("%.3f", search.getGeneration() * 1000.0 / getMillisecondsElapsed())
                                );
                                controller.getTxtLastImprov().setText(getTimeSinceImprovement());
                                controller.getTxtAvgImprov().setText(getAverageImprovementTime());
                                controller.getTxtCores().setText(Runtime.getRuntime().availableProcessors() + "");
                            });
                        }

                        Thread.sleep(SLEEP_TIME);
                    }
                } catch (InterruptedException ex) {
                    log.error("{}: ", ex::getMessage);
                }
                updating.unlock();
                return null;
            }
        };
    }

    /**
     * Convert seconds to a minute and seconds strings
     * @param elapsed time elapsed as a long in seconds
     * @return Time elapsed
     */
    private String timeElapsed(long elapsed) {
        double seconds = (elapsed % 60000) / 1000.0;
        elapsed /= 1000;
        long minutes = elapsed / 60;

        // If over one minute, start showing minutes
        String format = minutes > 0 ? "%1$dm %2$.3fs" : "%2$.3fs";
        return String.format(format, minutes, seconds);
    }

    /**
     * Gets seconds elapsed in the search
     * @return Seconds as a long
     */
    private long getMillisecondsElapsed() {
        if (search == null || search.getStartTime() == null) {
            return 0L;
        }
        return Math.max(search.getElapsedDuration(), 1);
    }

    /**
     * Gets time elapsed as a string
     * @return Time elapsed as a string
     */
    private String getTimeElapsed() {
        return timeElapsed(getMillisecondsElapsed());
    }

    /**
     * Gets time since the last improvement
     * @return Time since the last improvement
     */
    private String getTimeSinceImprovement() {
        if (search == null || search.getLastImproveTime() == null) {
            return timeElapsed(0L);
        }
        return timeElapsed(Math.max((search.getElapsedDuration() + search.getStartTime() - search.getLastImproveTime()), 1));
    }

    /**
     * Gets time since the last improvement
     * @return Time since the last improvement
     */
    private String getAverageImprovementTime() {
        if (search == null || search.getAverageImproveDuration() == null) {
            return timeElapsed(0L);
        }
        return timeElapsed(Math.max(search.getAverageImproveDuration(), 1));
    }

    public WorkspaceService getWorkspaceService() {
        return workspaceService;
    }
}
