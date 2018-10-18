/**
 * Copyright 2018 Iconic
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
            if (value == null) {
                continue;
            }
            if (oldMin == null || value.doubleValue() < oldMin.doubleValue()) {
                oldMin = value;
            }

            if (oldMax == null || value.doubleValue() > oldMax.doubleValue()) {
                oldMax = value;
            }
        }

        // If the whole list has null values, just return
        if (oldMin == null || oldMax == null) {
            return values;
        }

        for (int i = 0; i < values.size(); i++) {
            if (values.get(i) == null) {
                continue;
            }
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