package org.iconic.ea.operator.primitive;

public class Tan extends ArithmeticPrimitive<Number> {
    public Tan() {
        super(
                args -> Math.tan(args.get(0)),
                1, "TAN"
        );
    }
}
