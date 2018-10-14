/**
 * Copyright (C) 2018 Iconic
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
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
            Number value = values.get(i);
            if (value == null) {
                continue;
            }
            Double doubleValue = values.get(i).doubleValue() + offset.doubleValue();
            values.set(i, doubleValue);
        }

        return values;
    }

    public void setOffset(Number offset) {
        this.offset = offset;
    }
}