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
package org.iconic.project.search;

import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import lombok.extern.log4j.Log4j2;
import java.util.concurrent.locks.Lock;

import org.iconic.project.Displayable;
import org.iconic.project.search.config.SearchConfigurationModel;
import org.iconic.project.search.io.SearchExecutor;
import org.iconic.project.search.io.SearchState;
import org.iconic.workspace.WorkspaceService;

/**
 * Worker service to update the GUI on the Start Search screen
 */
@Log4j2
public class SearchStatistics extends Service<Void> {

    private static final int START_PAUSE = 50;
    private static final int SLEEP_TIME = 250;

    private final WorkspaceService workspaceService;
    private StartSearchController controller;
    private SearchExecutor<?> executor;
    private Lock updating;

    /**
     * Worker service to update the GUI on the Start Search screen
     * @param workspaceService The workspace service
     * @param controller The controller
     * @param executor Current executor
     * @param updating Lock for updating
     */
    public SearchStatistics(
            WorkspaceService workspaceService,
            StartSearchController controller,
            SearchExecutor<?> executor,
            Lock updating
    ) {
        this.workspaceService = workspaceService;
        this.controller = controller;
        this.executor = executor;
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
                    do {
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

                        if (activeSearch.getSearchExecutor().get() == executor) {
                            update(executor);
                        }

                        Thread.sleep(SLEEP_TIME);
                    }
                    while (executor.getState() == SearchState.RUNNING);

                    Thread.sleep(SLEEP_TIME);
                } catch (InterruptedException ex) {
                    log.error("{}: ", ex::getMessage);
                }
                update(executor);
                updating.unlock();
                return null;
            }
        };
    }

    public void update(SearchExecutor<?> executor) {
        Platform.runLater(() -> {
            controller.getTxtTime().setText(getTimeElapsed());
            controller.getTxtGen().setText(executor.getGeneration() + "");
            controller.getTxtGenSec().setText(getGensPerSecond());
            controller.getTxtLastImprov().setText(getTimeSinceImprovement());
            controller.getTxtAvgImprov().setText(getAverageImprovementTime());
            controller.getTxtCores().setText(Runtime.getRuntime().availableProcessors() + "");
        });
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
     * Calculate the different between the current time and when the executor was last updated.
     * This is needed when we use massive datasets, to get the stats updating in real-time.
     * @return Difference
     */
    private long getDiff() {
        return executor.getState() == SearchState.RUNNING ? System.currentTimeMillis() - executor.getLastUpdateTime() : 0;
    }

    /**
     * Gets seconds elapsed in the executor, at least 1 to prevent division by zero
     */
    private long getMillisecondsElapsed() {
        if (executor == null || executor.getElapsedDuration() == null) {
            return 0L;
        }
        return Math.max(executor.getElapsedDuration() + getDiff(), 1L);
    }

    /**
     * Gets time elapsed as a string
     * @return Time elapsed as a string
     */
    private String getTimeElapsed() {
        return timeElapsed(getMillisecondsElapsed());
    }

    /**
     * Get the generations per second as text
     * @return Gen / Second
     */
    private String getGensPerSecond() {
        long ms = getMillisecondsElapsed();
        double amount = ms > 0 ? executor.getGeneration() * 1000.0 / ms : 0.0;
        return String.format("%.3f", amount);
    }

    /**
     * Gets time since the last improvement
     * @return Time since the last improvement
     */
    private String getTimeSinceImprovement() {
        if (executor == null || executor.getTimeSinceImprovement() == null) {
            return timeElapsed(0L);
        }
        return timeElapsed(executor.getTimeSinceImprovement() + getDiff());
    }

    /**
     * Gets time since the last improvement
     * @return Time since the last improvement
     */
    private String getAverageImprovementTime() {
        if (executor == null || executor.getAverageImproveDuration() == null) {
            return timeElapsed(0L);
        }
        return timeElapsed(executor.getAverageImproveDuration());
    }

    public WorkspaceService getWorkspaceService() {
        return workspaceService;
    }
}
