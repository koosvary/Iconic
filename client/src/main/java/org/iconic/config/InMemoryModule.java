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
