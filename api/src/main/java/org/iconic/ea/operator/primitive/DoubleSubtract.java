package org.iconic.ea.operator.primitive;

public class DoubleSubtract extends FunctionalPrimitive<Double> {
    public DoubleSubtract() {
        super(
                args -> args.stream().reduce(1d, (a, b) -> a - b),
                2, "-"
        );
    }
}
