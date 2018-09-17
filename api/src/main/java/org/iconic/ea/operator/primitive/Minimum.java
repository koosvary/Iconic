package org.iconic.ea.operator.primitive;

public class Minimum extends ArithmeticPrimitive<Number> {
    public Minimum() {
        super(
                args -> Math.min(args.get(0),args.get(1)),
                2, "MIN"
        );
    }
}
