package org.iconic.control;

import javafx.scene.control.Tab;

public class WorkspaceTab extends Tab {
    private TabType tabType;

    public WorkspaceTab() {
        super();
        this.tabType = TabType.OTHER;
    }

    public TabType getTabType() {
        return tabType;
    }

    public void setTabType(TabType tabType) {
        this.tabType = tabType;
    }

    public enum TabType {
        DATASET,
        SEARCH,
        OTHER
    }
}
