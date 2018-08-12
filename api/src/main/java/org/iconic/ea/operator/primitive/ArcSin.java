package org.iconic.ea.operator.primitive;

public class ArcSin extends ArithmeticPrimitive<Number> {
    public ArcSin() {
        super(
                args -> Math.asin(args.get(0)),
                1, "ASIN"
        );
    }
}
