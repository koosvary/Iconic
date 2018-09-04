package org.iconic.ea.operator.primitive;

public class Ceiling extends ArithmeticPrimitive<Number> {
    public Ceiling() {
        super(
                args -> Math.ceil(args.get(0)),
                1, "CEIL"
        );
    }
}
