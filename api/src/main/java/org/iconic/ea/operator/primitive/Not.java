package org.iconic.ea.operator.primitive;

public class Not extends ArithmeticPrimitive<Number> {
    public Not() {
        super(
                args -> (args.get(0) > 0) ? 0.d : 1.d,
                1, "XOR"
        );
    }
}
