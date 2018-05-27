package org.iconic.ea.operator.primitive;

public class Power extends ArithmeticPrimitive<Number> {
    public Power() {
        super(
                args -> Math.pow(args.get(0), args.get(1)),
                2, "^"
        );
    }
}
