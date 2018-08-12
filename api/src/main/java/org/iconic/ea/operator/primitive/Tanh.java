package org.iconic.ea.operator.primitive;


//A sigmoid squashing function
public class Tanh extends ArithmeticPrimitive<Number> {
    public Tanh() {
        super(
                args -> Math.tanh(args.get(0)),
                1, "gauss"
        );
    }
}
