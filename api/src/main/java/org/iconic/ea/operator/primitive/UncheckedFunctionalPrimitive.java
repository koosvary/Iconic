package org.iconic.ea.operator.primitive;

import java.util.List;

@FunctionalInterface
public interface UncheckedFunctionalPrimitive<T> {
    T apply(List<T> args);
}
