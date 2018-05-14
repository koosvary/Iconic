package org.iconic.ea.operator.primitive;

public class DoubleDivide extends FunctionalPrimitive<Double> {
    public DoubleDivide() {
        super(
                args -> args.stream().reduce(1d, (a, b) -> a / b),
                2, "/"
        );
    }
}
