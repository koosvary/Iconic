package org.iconic.ea.operator.primitive;


public class StepFunction extends ArithmeticPrimitive<Number> {
    public StepFunction() {
        super(
                args -> args.get(0) > 0 ? 1.d : 0.d,
                1, "step"
        );
    }
}
