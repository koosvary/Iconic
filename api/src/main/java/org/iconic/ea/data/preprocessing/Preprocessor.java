package org.iconic.ea.data.preprocessing;

import java.util.List;

public abstract class Preprocessor<T> {
    private TransformType transformType;

    public abstract List<T> apply(List<T> values);

    public TransformType getTransformType() {
        return transformType;
    }

    public void setTransformType(TransformType transformType) {
        this.transformType = transformType;
    }
}
