/**
 * @author Harry Barden <harry@barden.com.au>
 */
package org.iconic.ea.operator.objective.error;

import lombok.extern.log4j.Log4j2;

import java.util.List;

@Log4j2
public class MeanSquaredError implements ErrorFunction {
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
