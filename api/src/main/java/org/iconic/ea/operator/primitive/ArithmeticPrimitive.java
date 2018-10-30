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
package org.iconic.ea.operator.primitive;

import java.util.List;
import java.util.function.Function;

public class ArithmeticPrimitive<T extends Number> extends FunctionalPrimitive<Double, Double> {
    public ArithmeticPrimitive(final Function<List<Double>, Double> lambda, final int arity, final String symbol, final String description, final Integer defaultComplexity) {
        super(lambda, arity, symbol, description, defaultComplexity);
    }

    public ArithmeticPrimitive(final Function<List<Double>, Double> lambda, final int arity, final String symbol, final String description) {
        super(lambda, arity, symbol, description);
    }
}
