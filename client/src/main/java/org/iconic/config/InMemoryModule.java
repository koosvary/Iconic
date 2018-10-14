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
package org.iconic.config;

import com.google.inject.AbstractModule;
import org.iconic.project.ProjectService;
import org.iconic.project.TransientProjectService;
import org.iconic.project.search.SearchService;
import org.iconic.project.search.TransientSearchService;
import org.iconic.views.DefaultViewService;
import org.iconic.views.ViewService;
import org.iconic.workspace.DefaultWorkspaceService;
import org.iconic.workspace.WorkspaceService;

/**
 * {@inheritDoc}
 * <p>
 * An in memory module defines services that are guaranteed not to persist any of their data for the next time the
 * application is opened.
 * </p>
 */
public class InMemoryModule extends AbstractModule {
    /**
     * {@inheritDoc}
     */
    @Override
    protected void configure() {
        bind(IconService.class).to(FontAwesomeIconService.class);
        bind(ProjectService.class).to(TransientProjectService.class);
        bind(SearchService.class).to(TransientSearchService.class);
        bind(ViewService.class).to(DefaultViewService.class);
        bind(WorkspaceService.class).to(DefaultWorkspaceService.class);
    }
}
