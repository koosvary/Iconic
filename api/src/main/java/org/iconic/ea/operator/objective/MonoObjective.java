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

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * <p>Defines a functional interface for an objective
 *
 * <p>
 * An objective is a measure used by an {@see org.iconic.ea.EvolutionaryAlgorithm} to determine the fitness
 * of chromosomes.
 *
 *
 */
public abstract class MonoObjective<T extends Comparable<T>> implements Objective<T> {
    public MonoObjective() {
        // Do nothing
    }
}
