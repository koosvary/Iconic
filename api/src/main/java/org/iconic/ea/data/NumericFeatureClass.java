package org.iconic.ea.data;

import org.iconic.ea.data.preprocessing.Normalise;

public class NumericFeatureClass extends FeatureClass<Number> {
    public NumericFeatureClass(boolean output) {
        super(output);
        getPreprocessors().add(new Normalise());
    }
}