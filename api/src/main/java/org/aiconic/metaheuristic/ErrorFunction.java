package org.aiconic.metaheuristic;

import org.aiconic.io.DataManager;

public class ErrorFunction {

    public static double calculateError(int sample, double assumedAnswer) {
        int index = DataManager.getFeatureSize();
        double expectedAnswer = DataManager.getInputVariable(sample, index - 1);

        //Debug.out("ErrorFunction    expectedAnswer = " + expectedAnswer + " assumedAnswer = " + assumedAnswer);
        return Math.abs(expectedAnswer - assumedAnswer);
    }
}