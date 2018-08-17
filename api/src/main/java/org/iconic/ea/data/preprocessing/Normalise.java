package org.iconic.ea.data.preprocessing;

import java.util.ArrayList;

public class Normalise {
    private static Number oldMin, oldMax;

    public static ArrayList<Number> apply(ArrayList<Number> values, Number newMin, Number newMax) {
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

        return values;
    }

    private static double map(Number value, Number oldMin, Number oldMax, Number newMin, Number newMax) {
        return newMin.doubleValue() +
                ((value.doubleValue() - oldMin.doubleValue())
                * (newMax.doubleValue() - newMin.doubleValue()))
                / (oldMax.doubleValue() - oldMin.doubleValue());
    }
}