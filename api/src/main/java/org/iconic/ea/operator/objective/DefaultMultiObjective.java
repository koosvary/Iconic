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
 * {@inheritDoc}
 * <p>
 * This multi-objective sums all of its goals together to produce the final fitness.
 */
public class DefaultMultiObjective extends MultiObjective<Double> {
    public DefaultMultiObjective(Collection<Objective<Double>> goals) {
        super(goals);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double apply(final Chromosome<Double> c) {
        double fitness = getGoals().get(0).apply(c);
        c.setFitness(fitness);
        return fitness;
    }
}
