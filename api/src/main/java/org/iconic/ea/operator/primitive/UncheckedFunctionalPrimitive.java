package org.iconic.ea.operator.primitive;

import java.util.List;

@FunctionalInterface
public interface UncheckedFunctionalPrimitive<T, R> {
    R apply(List<T> args);
}
