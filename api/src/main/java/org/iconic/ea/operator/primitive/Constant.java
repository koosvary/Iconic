package org.iconic.ea.operator.primitive;

public class Constant<T> extends FunctionalPrimitive<T, T> {
    public Constant(final T value) {
        super(args -> value, 0, value.toString());
    }
}
