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
     * </p>
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
