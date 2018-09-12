package org.iconic.ea.operator.primitive;

public class Modulo extends ArithmeticPrimitive<Number> {
    public Modulo() {
        super(
                args -> args.get(0) % args.get(1),
                2, "MOD"
        );
    }
}
