package org.iconic.ea.operator.primitive;

public class TwoArcTan extends ArithmeticPrimitive<Number> {
    public TwoArcTan() {
        super(
                args -> Math.atan2(args.get(0),args.get(1)),
                2, "ATAN2"
        );
    }
}
