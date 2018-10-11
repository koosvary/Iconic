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
import org.iconic.ea.chromosome.cartesian.CartesianChromosome;

import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>Defines a functional interface for an objective</p>
 *
 * <p>
 * An objective is a measure used by an {@see org.iconic.ea.EvolutionaryAlgorithm} to determine the fitness
 * of chromosomes.
 * </p>
 *
 */
public abstract class MultiObjective<T extends Comparable<T>> implements Objective<T> {
    private final List<Objective<T>> goals;

    public MultiObjective(Collection<Objective<T>> goals) {
        this.goals = new LinkedList<>(goals);
    }

    public Collection<Objective<T>> getGoals() {
        return goals;
    }

    public void addGoal(final MonoObjective<T> goal) {
        getGoals().add(goal);
    }

    public void removeGoal(final MonoObjective<T> goal) {
        getGoals().remove(goal);
    }

    public void removeGoal(int index) {
        getGoals().remove(index);
    }

    /**
     * {@inheritDoc}
     * @return
     */
    @Override
    public boolean isNotWorse(double x, double y) {
        double epsilon = 1E-6;
        return x <= y || Math.abs(x - y) < epsilon;
    }
}
