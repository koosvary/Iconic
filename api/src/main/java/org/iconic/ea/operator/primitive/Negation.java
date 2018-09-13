package org.iconic.ea.operator.primitive;

public class Negation extends ArithmeticPrimitive<Number>  {
    public Negation() {
        super(
                args -> -args.get(0),
                1, "-"
        );


    }
}
