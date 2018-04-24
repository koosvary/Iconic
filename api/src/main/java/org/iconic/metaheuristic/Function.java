package org.iconic.metaheuristic;

import java.util.*;

import org.iconic.io.DataManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Function {
    private static final Logger logger = LogManager.getLogger(Function.class);
    private FunctionType functionType;
    private double constant;
    private int inputVariableIndex;

    Function(String shortCode) {
        logger.traceEntry();

        functionType = FunctionType.getFunctionType(shortCode);
        logger.debug("shortCode = {}\t functionType = {}", () -> shortCode, () -> functionType);

        if (functionType.children() == 0) {
            Random rand = new Random();
            int upperBound = (DataManager.getFeatureSize() - 1 > 0) ? DataManager.getFeatureSize() - 1 : 1;
            inputVariableIndex = rand.nextInt(upperBound);
            constant = rand.nextDouble() * 2;
        }

        logger.traceExit();
    }

    public double calculate(int sample, Node[] children) {
        if (children.length == 0) {
            if (functionType == FunctionType.INPUT_VARIABLE)
                return DataManager.getInputVariable(sample, inputVariableIndex);
            if (functionType == FunctionType.CONSTANT || functionType == FunctionType.INTEGER_CONSTANT)
                return constant;
        }

        double calculation = children[0].calculate(sample);
        for (int i = 1; i < children.length; i++) {
            Node n = children[i];
            switch (functionType.symbol()) {
                case "+":
                    calculation += n.calculate(sample);
                    break;
                case "-":
                    calculation -= n.calculate(sample);
                    break;
                case "*":
                    calculation *= n.calculate(sample);
                    break;
                case "/":
                    calculation /= n.calculate(sample);
                    break;
                case "^":
                    calculation = Math.pow(calculation, n.calculate(sample));
                    break;
                case "<":
                    calculation = Math.min(calculation, n.calculate(sample));
                    break;
                case ">":
                    calculation = Math.max(calculation, n.calculate(sample));
                    break;
                case "SQRT":
                    calculation = Math.sqrt(calculation);
                    break;
                case "E":
                    calculation = Math.exp(calculation);
                    break;
                case "SIN":
                    calculation = Math.sin(calculation);
                    break;
                case "COS":
                    calculation = Math.cos(calculation);
                    break;
                case "TAN":
                    calculation = Math.tan(calculation);
                    break;
                case "ABS":
                    calculation = Math.abs(calculation);
                    break;
                case "LOG":
                    calculation = Math.log(calculation);
                    break;
            }
        }

        return calculation;
    }

    public int getChildren() {
        return functionType.children();
    }
}