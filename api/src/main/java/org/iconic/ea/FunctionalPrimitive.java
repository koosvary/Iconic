package org.iconic.ea;

import java.util.List;
import java.util.function.Function;

public class FunctionalPrimitive<T> {
    private String shortCode;
    private final Function<List<T>, T> lambda;
    private final int arity; // Argument Size
    private int complexity;

    public FunctionalPrimitive(final Function<List<T>, T> lambda, int arity) {
        this.lambda = lambda;
        this.arity = arity;
    }

    public T apply(List<T> args) {
        assert(args.size() >= getArity());

        return lambda.apply(args);
    }

    public int getArity() {
        return arity;
    }

    private Function<List<T>, T> getLambda() {
        return lambda;
    }

    public String getShortCode() { return shortCode; }

    public void setShortCode(String shortCode) { this.shortCode = shortCode; }

    public String toString() { return getShortCode(); }
}
