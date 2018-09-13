package org.iconic.ea.operator.primitive;


//A sigmoid squashing function
public class GaussianFunction extends ArithmeticPrimitive<Number> {
    public GaussianFunction() {
        super(
                args -> Math.exp(-Math.pow(args.get(0),2)),
                1, "GAUSS"
        );
    }
}
