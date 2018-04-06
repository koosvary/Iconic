package org.aiconic.metaheuristic;

import java.util.*;
import org.aiconic.io.DataManager;

public class Function
{
    private FunctionType functionType;
    private double constant;
    private int inputVariableIndex;
    
    Function(String shortCode)
    {
        functionType = FunctionType.getFunctionType(shortCode);
        //Debug.out("Function Function() shortCode = " + shortCode + "   functionType = " + functionType);
        
        if (functionType.children() == 0)
        {
            Random rand = new Random();
            inputVariableIndex = rand.nextInt(DataManager.getFeatureSize() - 1);
            constant = rand.nextDouble() * 2;
        }
    }
    
    public double calculate(int sample, Node[] children)
    {
        if (children.length == 0)
        {
            if (functionType == FunctionType.INPUTVARIABLE)
                return DataManager.getInputVariable(sample, inputVariableIndex);
            if (functionType == FunctionType.CONSTANT || functionType == FunctionType.INTEGERCONSTANT)
                return constant;
        }
        
        double calculation = children[0].calculate(sample);
        for (int i = 1; i < children.length; i++) 
        {
            Node n = children[i];
            switch(functionType.symbol())
            {
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
    
    public int getChildren()
    {
        return functionType.children();
    }
}