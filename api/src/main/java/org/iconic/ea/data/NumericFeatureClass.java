package org.iconic.ea.data;

import org.iconic.ea.data.preprocessing.Normalise;
import org.iconic.ea.data.preprocessing.Offset;

public class NumericFeatureClass extends FeatureClass<Number> {
    public NumericFeatureClass(boolean output) {
        super(output);
        getPreprocessors().add(new Normalise());
        getPreprocessors().add(new Offset());
    }
}