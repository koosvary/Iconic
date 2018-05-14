package org.iconic.ea.operator.primitive;

public class DoubleAddition extends FunctionalPrimitive<Double> {
    public DoubleAddition() {
        super(
                args -> args.stream().mapToDouble(Double::doubleValue).sum(),
                2, "+"
        );
    }
}
