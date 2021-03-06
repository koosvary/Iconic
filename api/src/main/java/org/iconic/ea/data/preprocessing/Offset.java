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

public class Offset extends Preprocessor<Number> {
    private Number offset;

    public Offset(Number offset) {
        this.offset = offset;
    }

    /**
     * <p>
     * Transforms an array of values by shifting the values by a given offset.
     *
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