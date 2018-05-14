package org.iconic.ea.operator.primitive;

public class DoubleProduct extends FunctionalPrimitive<Double> {
    public DoubleProduct() {
        super(
                args -> args.stream().reduce(1d, (a, b) -> a * b),
                2, "*"
        );
    }
}
