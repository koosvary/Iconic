/**
 * @author Harry Barden <harry@barden.com.au>
 */
package org.iconic.ea.operator.objective.error;

import java.util.List;

@FunctionalInterface
public interface ErrorFunction {
    double apply(final List<Double> calculated, final List<Double> expected);
}
