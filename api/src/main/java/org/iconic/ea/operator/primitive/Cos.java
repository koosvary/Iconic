package org.iconic.ea.operator.primitive;

public class Cos extends ArithmeticPrimitive<Number> {
    public Cos() {
        super(
                args -> Math.cos(args.get(0)),
                1, "COS"
        );
    }
}
