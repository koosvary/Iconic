/**
 * Copyright (C) 2018 Iconic
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.iconic.ea.operator.objective;

import org.iconic.ea.chromosome.Chromosome;

/**
 * Defines a functional interface for an objective
 * <p>
 * An objective is a measure used by an {@see org.iconic.ea.EvolutionaryAlgorithm} to determine the fitness
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
        double epsilon = 1E-6;
        return x <= y || Math.abs(x - y) < epsilon;
    }
}
