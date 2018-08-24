package org.iconic.ea.data.preprocessing;

public class Transformation {
    private String header;
    private TransformType transform;

    public Transformation(String header, TransformType transform) {
        this.header = header;
        this.transform = transform;
    }

    public String getHeader() {
        return header;
    }

    public TransformType getTransform() {
        return transform;
    }
}
