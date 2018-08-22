package org.iconic.ea.operator.primitive;


//A sigmoid squashing function
public class LogisticFunction extends ArithmeticPrimitive<Number> {
    public LogisticFunction() {
        super(
                args -> 1/(1+Math.exp(args.get(0))),
                1, "LOGISTIC"
        );
    }
}
