package org.iconic.ea.operator.primitive;

public class ArcTan extends ArithmeticPrimitive<Number> {
    public ArcTan() {
        super(
                args -> Math.atan(args.get(0)),
                1, "ATAN"
        );
    }
}
