package org.iconic.ea.operator.primitive;

public class IfThenElse extends ArithmeticPrimitive<Number> {
    public IfThenElse() {
        super(
                args -> args.get(0) > 0 ? args.get(1) : args.get(2),
                3, "if"
        );
    }
}
