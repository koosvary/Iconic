package org.iconic.ea.operator.error;

import java.util.List;

@FunctionalInterface
public interface ErrorFunction<T> {
    double apply(final List<T> calculated, final List<T> expected);
}