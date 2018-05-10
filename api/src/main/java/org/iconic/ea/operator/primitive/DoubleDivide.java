package org.iconic.ea.operator.primitive;

import java.util.List;

public class DoubleDivide extends FunctionalPrimitive<Double> {
    public DoubleDivide() {
        super(
                (List<Double> args) ->
                        args.stream().reduce(1d, (a, b) -> a / b),
                2
        );

        setShortCode("/");
        setComplexity(1);
    }
}
