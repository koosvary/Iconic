package org.iconic.ea.data.preprocessing;

/**
 * A transformation object stores a string corresponding with the header (or feature) that a transform was
 * applied to, as well as the transformation type (e.g. smoothed, normalised, etc.).
 */
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
