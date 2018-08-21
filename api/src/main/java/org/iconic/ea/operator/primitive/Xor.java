package org.iconic.ea.operator.primitive;

public class Xor extends ArithmeticPrimitive<Number> {
    public Xor() {
        super(
                args -> (args.get(0) > 0 ^ args.get(1) > 0) ? 1.d : 0.d,
                2, "Xor"
        );
    }
}
