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

    public List<Objective<T>> getGoals() {
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
