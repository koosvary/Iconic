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
package org.iconic.ea.operator.primitive;

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
    private final int defaultComplexity;

    public FunctionalPrimitive(Function<List<T>, R> lambda, int arity, String symbol, String description, int defaultComplexity) {
        this.lambda = lambda;
        this.arity = arity;
        this.symbol = symbol;
        this.description = description;
        this.defaultComplexity = defaultComplexity;
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
}
