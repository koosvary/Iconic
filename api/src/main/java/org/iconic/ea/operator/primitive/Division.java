package org.iconic.ea.operator.primitive;

public class Division extends ArithmeticPrimitive<Number> {
    public Division() {
        super(
                // This isnt working atm
                args -> (Double) args.stream().reduce(1.d, (a, b) -> {
                    if (b == 0) {
                        return 1.d;
                    }

                    return a / b;
                }),
                2, "/"
        );
    }
}
