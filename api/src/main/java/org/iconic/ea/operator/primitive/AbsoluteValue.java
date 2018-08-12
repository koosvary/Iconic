package org.iconic.ea.operator.primitive;

public class AbsoluteValue extends ArithmeticPrimitive<Number> {
    public AbsoluteValue() {
        super(
                args -> Math.abs(args.get(0)),
                1, "ABS"
        );
    }
}
