package org.iconic.ea.data.preprocessing;

import java.util.ArrayList;

public class Offset {
    /**
     * <p>
     * Transforms an array of values by shifting the values by a given offset.
     * </p>
     *
     * @param values the array that will be transformed.
     */
    public static ArrayList<Number> apply(ArrayList<Number> values, Number offset) {
        for (int i = 0; i < values.size(); i++) {
            Double value = values.get(i).doubleValue() + offset.doubleValue();
            values.set(i, value);
        }

        return values;
    }
}