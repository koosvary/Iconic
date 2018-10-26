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
 *
 */
@Log4j2
public abstract class ErrorBasedObjective extends MonoObjective<Double> {
    private final ErrorFunction lambda;
    private final DataManager<Double> dataManager;
    private List<Double> expectedResults;

    /**
     * <p>
     * Constructs a new ErrorBasedObjective with the provided error function and samples.
     *
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
     * <p>Returns the dataset used by this objective.
     *
     * @return the dataset used by this objective
     */
    protected DataManager<Double> getDataManager() {
        return dataManager;
    }

    /**
     * <p>Returns the error function used by this objective.
     *
     * @return the error function used by this objective
     */
    protected ErrorFunction getLambda() {
        return lambda;
    }

    /**
     * <p>Returns the expected results for the samples used by this objective.
     *
     * @return the expected results for the samples used by this objective
     */
    protected List<Double> getExpectedResults() {
        return expectedResults;
    }
}
