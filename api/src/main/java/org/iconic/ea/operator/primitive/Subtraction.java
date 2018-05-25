package org.iconic.ea.operator.primitive;

public class Subtraction extends ArithmeticPrimitive<Number> {
    public Subtraction() {
        super(
                // Such a dodgey way to do this. It counts the default 0 if the array is empty in the subtraction.
                // Therfore its actually 0 - args, so i have done (0 - arg[1] - arg[0]) * -1
                args -> args.stream().reduce(0.d, (a, b) -> b - a) * -1,
                2, "-"
        );


    }
}
