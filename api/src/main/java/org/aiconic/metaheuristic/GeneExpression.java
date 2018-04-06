package org.aiconic.metaheuristic;

import java.util.*;
import org.aiconic.io.DataManager;

public class GeneExpression
{
    private Node root;
    private String[] expression;
    private int complexity;
    private double fit;
    
    public GeneExpression()
    {
        generateExpression();
        generateTree();
    }
    
    public GeneExpression(String[] expression)
    {
    }
    
    public void generateTree()
    {
        root = new Node(expression);
    }
    
    public boolean testCalculate()
    {
        Random rand = new Random();
        
        return rand.nextInt(10) == 0;
    }
    
    public void generateExpression()
    {
        //Debug.out("GeneExpression   generateExpression()");
        Random rand = new Random();
        int headerLength = rand.nextInt(15);
        int numberOfAttributes;
        int tailLength = headerLength + 1;
        int numberOfFunctions = FunctionType.values().length;
        String symbol;
        expression = new String[headerLength + tailLength];
        complexity = 0;
        
        for (int i = 0; i < headerLength; i++) 
        {
            int n = rand.nextInt(numberOfFunctions); 
            symbol = FunctionType.values()[n].symbol();
            
            expression[i] = symbol;
        }
        
        for (int i = headerLength; i < headerLength + tailLength; i++)
        {
            int n = rand.nextInt(3);
            symbol = FunctionType.values()[n].symbol();
            expression[i] = symbol;
        }
        
        String output = "";
        
        for (String s : expression)
            output += s + ", ";
        
        //Debug.out("GeneExpression   expression = " + output);
    }
    
    public void mutateExpression()
    {
        
    }
    
    public double calculate()
    {
        double totalError = 0;
        
        for (int sample = 0; sample < DataManager.getSampleSize(); sample++)
        {
            totalError += ErrorFunction.calculateError(sample, root.calculate(sample));
        }
        totalError /=DataManager.getSampleSize();
        fit = totalError;
        
        return totalError;
    }
    
    
    
    public String getExpressionString()
    {
        String output = expression[0];
        
        for (int i = 1; i < expression.length; i++)
            output += ", " + expression[i];
        
        return output;
    }
    
    public String[] getExpression()
    {
        return expression;
    }
    
    public void setExpression(String[] expression)
    {
        this.expression = expression;
    }
    
    public double getFit()
    {
        return fit;
    }
    
    public int getSize()
    {
        return expression.length;
    }
    
    public int getComplexiy()
    {
        return complexity;
    }
}