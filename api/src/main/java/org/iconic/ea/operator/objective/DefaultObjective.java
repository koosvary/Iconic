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

import lombok.extern.log4j.Log4j2;
import org.iconic.ea.chromosome.Chromosome;
import org.iconic.ea.data.DataManager;
import org.iconic.ea.operator.objective.error.ErrorFunction;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * {@inheritDoc}
 * <p>
 * A default objective that uses a chromosome's error as the fitness.
 * </p>
 */
@Log4j2
public class DefaultObjective<T> extends ErrorBasedObjective<T> {

    /**
     * <p>Constructs a new DefaultObjective</p>
     *
     * @param lambda      The error function to apply
     * @param dataManager The samples to use with the error function
     */
    public DefaultObjective(final ErrorFunction lambda, final DataManager<T> dataManager) {
        super(lambda, dataManager);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double apply(final Chromosome<T> c) {
        List<Map<Integer, T>> results = c.evaluate(getDataManager());
        List<Double> summedResults = new ArrayList<>(results.size());

        // Convert results into a list with each output summed
        results.forEach(result -> summedResults.add(
                result.values().stream().mapToDouble(i -> (Double) i).sum()
        ));

        final double fitness = getLambda().apply(summedResults, (List<Double>) getExpectedResults());
        c.setFitness(fitness);

        return fitness;
    }
}
