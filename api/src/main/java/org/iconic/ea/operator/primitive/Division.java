package org.iconic.ea.operator.primitive;

public class Division extends ArithmeticPrimitive<Number> {
    public Division() {
        super(
                    args -> {
                        final double delta = 0.001;
                        double identity = args.get(0);

                        for (int i = 1; i < args.size(); ++i) {
                            if (args.get(i) < delta + 0.d && args.get(i) > 0.d - delta) {
                                return 1.d;
                            }

                            identity /= args.get(i);
                        }

                        return identity;
                    },
                2, "/"
        );
    }
}
