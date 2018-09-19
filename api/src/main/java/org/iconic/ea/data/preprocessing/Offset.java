package org.iconic.ea.data.preprocessing;

import java.util.List;

public class Offset extends Preprocessor<Number> {
    private Number offset;

    public Offset(Number offset) {
        this.offset = offset;
    }

    /**
     * <p>
     * Transforms an array of values by shifting the values by a given offset.
     * </p>
     *
     * @param values the array that will be transformed.
     */
    public List<Number> apply(List<Number> values) {
        for (int i = 0; i < values.size(); i++) {
            Double value = values.get(i).doubleValue() + offset.doubleValue();
            values.set(i, value);
        }

        return values;
    }

    public void setOffset(Number offset) {
        this.offset = offset;
    }
}