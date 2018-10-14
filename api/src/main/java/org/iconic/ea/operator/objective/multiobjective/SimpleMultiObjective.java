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
        double fitness = getGoals().stream()
                .mapToDouble(goal -> goal.apply(c))
                .sum() / getGoals().size();
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
