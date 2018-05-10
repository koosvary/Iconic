package org.iconic.ea.operator.error;

import java.util.List;

public class MeanSquaredError implements ErrorFunction<Double> {

    @Override
    public double apply(final List<Double> calculated, final List<Double> expected) {
        assert(calculated.size() == expected.size());

        double sum = 0;

        for (int i = 0; i < calculated.size(); i++)
            sum += (expected.get(i) - calculated.get(i)) * (expected.get(i) - calculated.get(i));

        sum /= calculated.size();

        return sum;
    }
}