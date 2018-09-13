package org.iconic.ea.operator.primitive;

public class ArcCos extends ArithmeticPrimitive<Number> {
    public ArcCos() {
        super(
                args -> Math.acos(args.get(0)),
                1, "ACOS"
        );
    }
}
