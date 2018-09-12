package org.iconic.ea.operator.primitive;

public class Exponential extends ArithmeticPrimitive<Number> {
    public Exponential() {
        super(
                args -> Math.exp(args.get(0)),
                1, "EXP"
        );
    }
}
