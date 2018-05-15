package org.iconic.ea.operator.primitive;

import java.util.List;
import java.util.function.Function;

public class FunctionalPrimitive<T, R> implements UncheckedFunctionalPrimitive<T, R> {
    private final Function<List<T>, R> lambda;
    private final int arity;
    private final String symbol;

    public FunctionalPrimitive(final Function<List<T>, R> lambda, final int arity, final String symbol) {
        this.lambda = lambda;
        this.arity = arity;
        this.symbol = symbol;
    }

    @Override
    public R apply(List<T> args) {
        assert(args.size() >= getArity());

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
}
