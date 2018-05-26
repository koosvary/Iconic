package org.iconic.ea.operator.primitive;

public class Subtraction extends ArithmeticPrimitive<Number> {
    public Subtraction() {
        super(
                args -> {
                    double identity = args.get(0);

                    for (int i = 1; i < args.size(); ++i) {
                        identity -= args.get(i);
                    }

                    return identity;
                },
                2, "-"
        );


    }
}
