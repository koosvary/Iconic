package org.iconic.ea.operator.primitive;

public class Sin extends ArithmeticPrimitive<Number> {
    public Sin() {
        super(
                args -> Math.sin(args.get(0)),
                1, "SIN"
        );
    }
}
