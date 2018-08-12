package org.iconic.ea.operator.primitive;

public class EqualTo extends ArithmeticPrimitive<Number> {
    public EqualTo() {
        super(
                args -> args.get(0).equals(args.get(1)) ? 1.d : 0.d,
                1, "=="
        );
    }
}
