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
/**
 * @author Harry Barden <harry@barden.com.au>
 */
package org.iconic.ea.operator.objective.error;

import lombok.extern.log4j.Log4j2;

import java.util.List;

@Log4j2
public class MeanSquaredError implements ErrorFunction {
    /**
     * {@inheritDoc}
     *
     * <p>
     * Applies the mean squared error method to calculate the amount of error to base the fitness on.
     *
     */
    @Override
    public double apply(final List<Double> calculated, final List<Double> expected) {
        assert (calculated.size() == expected.size());

        final int numValues = calculated.size();

        double sum = 0;

        for (int i = 0; i < numValues; i++) {
            final double error = (expected.get(i) - calculated.get(i));
            sum += error * error;
        }

        sum /= numValues;
        return sum;
    }
}
