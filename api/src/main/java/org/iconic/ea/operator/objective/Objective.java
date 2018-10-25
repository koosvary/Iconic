/**
 * Copyright 2018 Iconic
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.iconic.ea.operator.objective;

import org.iconic.ea.chromosome.Chromosome;

/**
 * Defines a functional interface for an objective
 * <p>
 * An objective is a measure used by an {@see org.iconic.ea.strategies.EvolutionaryAlgorithm} to determine the fitness
 * of chromosomes.
 *
 * @param <T> The type of the {@link org.iconic.ea.chromosome.Chromosome chromosome's} input and output
 */
@FunctionalInterface
public interface Objective<T extends Comparable<T>> {
    /**
     * Applies this objective to the given {@link org.iconic.ea.chromosome.Chromosome chromosome}.
     *
     * @param c The chromosome to apply this objective to
     * @return The fitness of the chromosome
     * @see org.iconic.ea.chromosome.Chromosome
     */
    double apply(final Chromosome<T> c);

    /**
     * Returns the worst fitness value possible for this objective.
     * By default if fitness values are ranked in ascending order negative infinity is returned,
     * otherwise the inverse is assumed and positive infinity is returned.
     *
     * @return The worst fitness value possible for the objective.
     */
    default double getWorstValue() {
        return (isNotWorse(Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY))
                ? Double.NEGATIVE_INFINITY
                : Double.POSITIVE_INFINITY;
    }

    /**
     * Returns true if the first value <i>is not worse</i> than the second value.
     *
     * @param x The value to test
     * @param y The value to test against
     * @return True if the first value is not worse than the second value.
     */
    default boolean isNotWorse(double x, double y) {
        return x <= y || isEqual(x, y);
    }

    /**
     * Returns true if the first value is <i>equivalent</i> to the second value.
     *
     * @param x The value to test
     * @param y The value to test against
     * @return True if the first value is equivalent to the second value.
     */
    default boolean isEqual(double x, double y) {
        double epsilon = 1E-6;
        return Math.abs(x - y) < epsilon;
    }
}
