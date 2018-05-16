package org.iconic.ea.operator.primitive;

public class Addition extends ArithmeticPrimitive<Number> {
    public Addition() {
        super(
                args -> args.stream().mapToDouble(Number::doubleValue).sum(),
                2, "+"
        );
    }
}
