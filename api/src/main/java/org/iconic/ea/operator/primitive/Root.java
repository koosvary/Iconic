package org.iconic.ea.operator.primitive;

public class Root extends ArithmeticPrimitive<Number> {
    public Root() {
        super(
                args -> {
                    final double delta = 0.001;
                    return (args.get(1) >= 0 + delta && args.get(1) <= 0 - delta)
                            ? Math.pow(args.get(0), 1 / args.get(1))
                            : 1;
                },
                2, "√ "
        );
    }
}
