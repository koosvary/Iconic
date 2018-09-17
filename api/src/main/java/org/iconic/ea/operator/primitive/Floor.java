package org.iconic.ea.operator.primitive;

public class Floor extends ArithmeticPrimitive<Number> {
    public Floor() {
        super(
                args -> Math.floor(args.get(0)),
                1, "FLOOR"
        );
    }
}
