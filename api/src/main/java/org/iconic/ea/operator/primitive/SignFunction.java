package org.iconic.ea.operator.primitive;


//A sigmoid squashing function
public class SignFunction extends ArithmeticPrimitive<Number> {
    public SignFunction() {
        super(
                args -> Math.signum(args.get(0)),
                1, "sgn"
        );
    }
}
