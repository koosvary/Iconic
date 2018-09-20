package org.iconic.ea.operator.primitive;

public class NaturalLog extends ArithmeticPrimitive<Number> {
    public NaturalLog() {
        super(
                args -> Math.log(args.get(0)),
                1, "ln"
        );
    }
}
