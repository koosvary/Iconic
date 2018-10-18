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

import javafx.beans.property.SimpleIntegerProperty;
import lombok.extern.log4j.Log4j2;

import java.util.List;
import java.util.function.Function;

/**
 * @param <T>
 * @param <R>
 */
@Log4j2
public class FunctionalPrimitive<T, R> implements UncheckedFunctionalPrimitive<T, R> {
    private final Function<List<T>, R> lambda;
    private final int arity;
    private final String symbol;
    private final String description;
    private final SimpleIntegerProperty complexity;
    private final int defaultComplexity;

    public FunctionalPrimitive(Function<List<T>, R> lambda, int arity, String symbol, String description, int defaultComplexity) {
        this.lambda = lambda;
        this.arity = arity;
        this.symbol = symbol;
        this.description = description;
        this.defaultComplexity = defaultComplexity;
        this.complexity = new SimpleIntegerProperty(defaultComplexity);
    }

    @Override
    public R apply(List<T> args) {
        assert (args.size() >= getArity());

        if (args.contains(null)) {
            log.warn("Null argument found: {}", args);
        }

        return lambda.apply(args);
    }

    public int getArity() {
        return arity;
    }

    @Override
    public String toString() {
        return getSymbol();
    }

    private Function<List<T>, R> getLambda() {
        return lambda;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getDescription() {
        if (arity > 0) {
            StringBuilder parameters = new StringBuilder();
            for (int i = 0; i < arity; i++) {
                parameters.append((char) ('a' + i)).append(", ");
            }
            parameters.setLength(parameters.length() - 2);

            return String.format("%s (%s):\n%s", symbol, parameters.toString(), description);
        }
        return description;
    }

    public int getDefaultComplexity() {
        return defaultComplexity;
    }

    public SimpleIntegerProperty getComplexity() {
        return complexity;
    }
}
