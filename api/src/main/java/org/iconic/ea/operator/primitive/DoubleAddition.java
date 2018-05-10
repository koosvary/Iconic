package org.iconic.ea.operator.primitive;

import java.util.List;

public class DoubleAddition extends FunctionalPrimitive<Double> {
    public DoubleAddition() {
        super(
                (List<Double> args) ->
                        args.stream().mapToDouble(Double::doubleValue).sum(),
                2
        );

        setShortCode("+");
        setComplexity(1);
    }
}
