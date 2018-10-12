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

/**
 * Worker service to update the GUI on the Start Search screen
 */
@Log4j2
public class SearchStatistics extends Service<Void> {

    private static final int START_PAUSE = 500;
    private static final int SLEEP_TIME = 250;

    private StartSearchController controller;
    private SearchExecutor search;
    private Lock updating;

    /**
     * Worker service to update the GUI on the Start Search screen
     * @param controller The controller
     * @param search Current search
     * @param updating Lock for updating
     */
    public SearchStatistics(StartSearchController controller, SearchExecutor search, Lock updating) {
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
                        Platform.runLater(() -> {

                            controller.getTxtTime().setText(timeElapsed());
                            controller.getTxtGen().setText(search.getGeneration() + "");
                            controller.getTxtGenSec().setText(
                                  String.format("%.3f", search.getGeneration() * 1.0 / getSecondsElapsed())
                            );
                            controller.getTxtCores().setText(Runtime.getRuntime().availableProcessors() + "");

                        });
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
     * Get the time elapsed in the current search as a string
     * @return Time elapsed
     */
    private String timeElapsed() {
        long elapsed = getSecondsElapsed();
        long seconds = elapsed % 60;
        long minutes = elapsed / 60;
        return String.format("%dm %ds", minutes, seconds);
    }

    /**
     * Gets seconds elapsed in the search
     * @return Seconds as a long
     */
    private long getSecondsElapsed() {
        if (search == null || search.getStartTime() == null) {
            return 0L;
        }
        return Math.max(search.getElapsedTime() / 1000, 1);
    }
}
