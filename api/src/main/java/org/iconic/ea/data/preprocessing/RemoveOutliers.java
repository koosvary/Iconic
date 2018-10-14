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

public class RemoveOutliers extends Preprocessor<Number> {
    private Number minCutoff, maxCutoff;

    public RemoveOutliers(Number minCutoff, Number maxCutoff) {
        this.minCutoff = minCutoff;
        this.maxCutoff = maxCutoff;
    }

    // Replaces any number outside of the range with null
    public List<Number> apply(List<Number> values) {
        System.out.println("RemoveOutliers  apply   values.size(): " + values.size());
        System.out.println("RemoveOutliers  apply   minCutoff: " + minCutoff + ", maxCutoff: " + maxCutoff);
        for (int i = 0; i < values.size(); i++) {
            Number value = values.get(i);

            if (value == null) {
                continue;
            }

            Double doubleValue = value.doubleValue();

            if (doubleValue < minCutoff.doubleValue() || doubleValue > maxCutoff.doubleValue()) {
                values.set(i, null);
            }
        }

        return values;
    }

    public void setMinCutoff(Number minCutoff) {
        this.minCutoff = minCutoff;
    }

    public void setMaxCutoff(Number maxCutoff) {
        this.maxCutoff = maxCutoff;
    }
}