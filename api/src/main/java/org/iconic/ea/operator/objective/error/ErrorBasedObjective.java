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
package org.iconic.ea.operator.objective.error;

import lombok.extern.log4j.Log4j2;
import org.iconic.ea.chromosome.Chromosome;
import org.iconic.ea.data.DataManager;
import org.iconic.ea.data.FeatureClass;
import org.iconic.ea.operator.objective.MonoObjective;
import org.iconic.ea.operator.objective.Objective;
import org.iconic.ea.operator.objective.error.ErrorFunction;

import java.util.*;
import java.util.stream.Collectors;

/**
 * {@inheritDoc}
 * <p>
 * An error based objective is an objective function based around an error function.
 * </p>
 */
@Log4j2
public abstract class ErrorBasedObjective extends MonoObjective<Double> {
    private final ErrorFunction lambda;
    private final DataManager<Double> dataManager;
    private List<Double> expectedResults;

    /**
     * <p>
     * Constructs a new ErrorBasedObjective with the provided error function and samples.
     * </p>
     *
     * @param lambda  The error function to apply
     * @param dataManager The dataset to apply the error function on
     */
    public ErrorBasedObjective(final ErrorFunction lambda, final DataManager<Double> dataManager) {
        super();
        this.lambda = lambda;
        this.dataManager = dataManager;
        this.expectedResults = new LinkedList<>();

        Map<String, FeatureClass<Number>> dataset = dataManager.getDataset();

        // Collect the expected answers
        List<FeatureClass<Number>> features = dataset.values().stream()
                .filter(FeatureClass::isOutput)
                .limit(1)
                .collect(Collectors.toList());

        expectedResults = features.get(0).getSamples().stream()
                .mapToDouble(Number::doubleValue).boxed()
                .collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public abstract double apply(final Chromosome<Double> c);

    /**
     * <p>Returns the dataset used by this objective.</p>
     *
     * @return the dataset used by this objective
     */
    protected DataManager<Double> getDataManager() {
        return dataManager;
    }

    /**
     * <p>Returns the error function used by this objective.</p>
     *
     * @return the error function used by this objective
     */
    protected ErrorFunction getLambda() {
        return lambda;
    }

    /**
     * <p>Returns the expected results for the samples used by this objective.</p>
     *
     * @return the expected results for the samples used by this objective
     */
    protected List<Double> getExpectedResults() {
        return expectedResults;
    }
}
