package org.iconic.ea.operator.objective.error;

import java.util.List;

/**
 * <p>Defines a functional interface for an error function</p>
 *
 * <p>
 * An error function is used to calculate the error between an evaluated output and an expected output.
 * </p>
 */
@FunctionalInterface
public interface ErrorFunction {
    /**
     * <p>Applies this error function to the given results</p>
     *
     * @param calculated The calculated results
     * @param expected The expected results
     * @return the amount of error between the calculated and expected results
     */
    double apply(final List<Double> calculated, final List<Double> expected);
}
