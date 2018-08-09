package org.iconic.ea.data.preprocessing;

import java.util.ArrayList;

public class Normalise<T extends Number> {
    private Number oldMin, oldMax, newMin, newMax;
    private boolean enabled = false;

    public void apply(ArrayList<Number> values) {
        oldMin = values.get(0);
        oldMax = values.get(0);

        for (Number value : values) {
            if (value.doubleValue() < oldMin.doubleValue()) {
                oldMin = value;
            }

            if (value.doubleValue() > oldMax.doubleValue()) {
                oldMax = value;
            }
        }

        for (int i = 0; i < values.size(); i++) {
            Number value = map(values.get(i), oldMin, oldMax, newMin, newMax);
            values.set(i, value);
        }
    }

    private double map(Number value, Number oldMin, Number oldMax, Number newMin, Number newMax) {
        return newMin.doubleValue() +
                ((value.doubleValue() - oldMin.doubleValue())
                * (newMax.doubleValue() - newMin.doubleValue()))
                / (oldMax.doubleValue() - oldMin.doubleValue());
    }

    public void setRange(Number newMin, Number newMax) { this.newMin = newMin; this.newMax = newMax; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
    public boolean isEnabled() { return enabled; }
}