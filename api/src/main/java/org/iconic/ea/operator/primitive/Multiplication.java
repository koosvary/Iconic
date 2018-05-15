package org.iconic.ea.operator.primitive;

public class Multiplication extends ArithmeticPrimitive<Number> {
    public Multiplication() {
        super(
                args -> args.stream().reduce(1d, (a, b) -> a * b),
                2, "*"
        );
    }
}
