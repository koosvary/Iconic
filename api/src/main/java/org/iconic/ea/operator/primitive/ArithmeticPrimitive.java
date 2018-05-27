package org.iconic.ea.operator.primitive;

import java.util.List;
import java.util.function.Function;

public class ArithmeticPrimitive<T extends Number> extends FunctionalPrimitive<Double, Double> {
    public ArithmeticPrimitive(final Function<List<Double>, Double> lambda, final int arity, final String symbol) {
        super(lambda, arity, symbol);
    }
}
