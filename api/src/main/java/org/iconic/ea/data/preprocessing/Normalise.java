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