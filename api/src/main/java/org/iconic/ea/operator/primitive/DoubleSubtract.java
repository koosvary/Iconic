package org.iconic.ea.operator.primitive;

import java.util.List;

public class DoubleSubtract extends FunctionalPrimitive<Double> {
    public DoubleSubtract() {
        super(
                (List<Double> args) ->
                        args.stream().reduce(1d, (a, b) -> a - b),
                2
        );

        setShortCode("-");
        setComplexity(1);
    }
}
