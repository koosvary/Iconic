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
package org.iconic.ea.operator.objective.multiobjective;

import org.iconic.ea.chromosome.Chromosome;
import org.iconic.ea.operator.objective.MultiObjective;
import org.iconic.ea.operator.objective.Objective;

import java.util.Collection;

/**
 * {@inheritDoc}
 * <p>
 * This multi-objective sums all of its goals together to produce the final fitness.
 */
public class SimpleMultiObjective extends MultiObjective<Double> {
    public SimpleMultiObjective(Collection<Objective<Double>> goals) {
        super(goals);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double apply(final Chromosome<Double> c) {
        getGoals().forEach(goal -> goal.apply(c));
        double fitness = getGoals().get(0).apply(c);
        c.setFitness(fitness);
        return fitness;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isNotWorse(double x, double y) {
        for (Objective<Double> goal : getGoals()) {
            if (goal.isNotWorse(x, y)) {
                return true;
            }
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isEqual(double x, double y) {
        for (Objective<Double> goal : getGoals()) {
            if (!goal.isEqual(x, y)) {
                return false;
            }
        }
        return true;
    }
}
