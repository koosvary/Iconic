package org.iconic.ea.operator.primitive;

public class Subtraction extends ArithmeticPrimitive<Number> {
    public Subtraction() {
        super(
                args -> args.stream().reduce(1d, (a, b) -> a - b),
                2, "-"
        );
    }
}
