package org.iconic.ea.operator.primitive;

public class LessThan extends ArithmeticPrimitive<Number> {
    public LessThan() {
        super(
                args -> args.get(0) < args.get(1) ? 1.d : 0.d,
                2, "<"
        );
    }
}
