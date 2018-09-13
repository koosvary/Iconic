package org.iconic.ea.operator.primitive;

public class Maximum extends ArithmeticPrimitive<Number> {
    public Maximum() {
        super(
                args -> Math.max(args.get(0),args.get(1)),
                2, "MAX"
        );
    }
}
