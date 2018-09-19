package org.iconic.ea.data.preprocessing;

import java.util.List;

public class Normalise extends Preprocessor<Number> {
    private Number newMin, newMax;

    public Normalise(Number newMin, Number newMax) {
        this.newMin = newMin;
        this.newMax = newMax;
    }

    public List<Number> apply(List<Number> values) {
        Number oldMin = values.get(0);
        Number oldMax = values.get(0);

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

        return values;
    }

    private double map(Number value, Number oldMin, Number oldMax, Number newMin, Number newMax) {
        return newMin.doubleValue() +
                ((value.doubleValue() - oldMin.doubleValue())
                * (newMax.doubleValue() - newMin.doubleValue()))
                / (oldMax.doubleValue() - oldMin.doubleValue());
    }

    public void setNewMin(Number newMin) {
        this.newMin = newMin;
    }

    public void setNewMax(Number newMax) {
        this.newMax = newMax;
    }
}