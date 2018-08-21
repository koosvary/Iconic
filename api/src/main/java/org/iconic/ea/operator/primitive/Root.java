package org.iconic.ea.operator.primitive;

import static java.lang.Math.abs;

public class Root extends ArithmeticPrimitive<Number> {
    public Root() {
        super(
                args -> {
                    final double delta = 0.001;
                    if (args.get(0) >= 0 + delta && args.get(0) >= 0 - delta) {
                        return Math.pow(args.get(0), 1 / args.get(1));
                    } else if (args.get(0) < 0 - delta) {
                        double result = Math.pow(abs(args.get(0)), 1 / args.get(1));
                        if (args.get(1) % 2 == 0) {
                            return result;
                        } else {
                            return -result;
                        }
                    }
                    return Double.POSITIVE_INFINITY;
                },
                2, "ROOT"
        );
    }
}
