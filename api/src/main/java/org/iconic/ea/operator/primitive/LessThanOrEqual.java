package org.iconic.ea.operator.primitive;

public class LessThanOrEqual extends ArithmeticPrimitive<Number> {
    public LessThanOrEqual() {
        super(
                args -> args.get(0) <= args.get(1) ? 1.d : 0.d,
                2, "<="
        );
    }
}
