package org.iconic.ea.operator.primitive;

public class SquareRoot extends ArithmeticPrimitive<Number> {
    public SquareRoot() {
        super(
                args -> Math.sqrt(args.get(0)),
                1, "SQRT"
        );
    }
}
