package org.iconic.ea.data.preprocessing;


import java.util.List;

public class Normalise {

    public static List<Double> apply(List<Double> values, double newMin, double newMax) {
        double oldMin = values.get(0);
        double oldMax = values.get(0);

        for (double value : values) {
            oldMin = Math.min(oldMin, value);
            oldMax = Math.max(oldMax, value);
        }

        for (int i = 0; i < values.size(); i++)
            values.set(i, map(values.get(i), oldMin, oldMax, newMin, newMax));

        return values;
    }

    private static double map(double value, double oldMin, double oldMax, double newMin, double newMax) {
        return newMin + ((value - oldMin) * (newMax - newMin)) / (oldMax - oldMin);
    }
}
