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
 * <p>Defines a functional interface for an objective</p>
 *
 * <p>
 * An objective is a measure used by an {@see org.iconic.ea.EvolutionaryAlgorithm} to determine the fitness
 * of chromosomes.
 * </p>
 *
 * @param <T> The type of the {@link org.iconic.ea.chromosome.Chromosome chromosome's} input and output
 */
@FunctionalInterface
public interface Objective<T> {
    /**
     * <p>Applies this objective to the given {@link org.iconic.ea.chromosome.Chromosome chromosome}</p>
     *
     * @param c The chromosome to apply this objective to
     * @return the fitness of the chromosome
     * @see org.iconic.ea.chromosome.Chromosome
     */
    double apply(final Chromosome<T> c);
}
