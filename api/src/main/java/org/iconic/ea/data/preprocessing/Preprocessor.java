package org.iconic.ea.data.preprocessing;

import java.util.ArrayList;

public abstract class Preprocessor<T> {
    private boolean enabled;

    protected Preprocessor() {
        enabled = false;
    }

    public abstract void apply(ArrayList<T> values);

    /**
     * <p>Sets whether or not this preprocessor should be used to the provided value</p>
     *
     * @param enabled True if the preprocessor should be used
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     *
     * @return
     */
    public boolean isEnabled() {
        return enabled;
    }
}
