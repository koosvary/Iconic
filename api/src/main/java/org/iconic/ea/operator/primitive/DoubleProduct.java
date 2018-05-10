package org.iconic.ea.operator.primitive;

import java.util.List;

public class DoubleProduct extends FunctionalPrimitive<Double> {
    public DoubleProduct() {
        super(
                (List<Double> args) ->
                        args.stream().reduce(1d, (a, b) -> a * b),
                2
        );

        setShortCode("*");
        setComplexity(1);
    }
}
