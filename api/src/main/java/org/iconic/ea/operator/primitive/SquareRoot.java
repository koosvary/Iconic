package org.iconic.ea.operator.primitive;

public class SquareRoot extends ArithmeticPrimitive<Number> {
    public SquareRoot() {
        super(
                args -> {
                    final double delta = 0.001;
                    return (args.get(1) >= 0 + delta && args.get(1) >= 0 - delta)
                            ? Math.sqrt(args.get(0))
                            : 1;
                },
                1, "SQRT"
        );
    }
}
