package org.iconic.ea.data.preprocessing;

import java.util.List;

public class Offset extends Preprocessor<Number>{
    private Number offset;

    /**
     * <p>
     * Transforms an array of values by shifting the values by a given offset.
     * </p>
     *
     * @param values the array that will be transformed.
     */
    @Override
    public void apply(List<Number> values) {
        for (int i = 0; i < values.size(); i++) {
            Double value = values.get(i).doubleValue() + offset.doubleValue();
            values.set(i, value);
        }
    }

    /**
     * <p>
     * Set the value that the array will be offset by.
     * </p>
     * @param offset The value to offset the array by.
     */
    public void setOffset(Number offset) { this.offset = offset; }
}