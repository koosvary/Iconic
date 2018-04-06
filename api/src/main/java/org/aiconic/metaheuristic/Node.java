package org.aiconic.metaheuristic;

import java.util.*;

public class Node
{
    private Function function;
    private Node[] children;
    
    Node() {}
    
    Node(String[] expression) 
    {
        generateTree(expression);
    }
    
    public void generateTree(String[] expression)
    {
        List<String> expressionList = new LinkedList<String>();
        
        for (String s : expression)
            expressionList.add(s);
        
        generateTree(expressionList);
    }
    
    public List<String> generateTree(List<String>  expression)
    {
        //Debug.out("Node generateTree()  expression = " + expression.toString());
        function = new Function(expression.remove(0));
        
        int numChildren = function.getChildren();
        children = new Node[numChildren];
        
        for (int i = 0; i < numChildren; i++)
            children[i] = new Node();
        
        for (int i = 0; i < numChildren; i++)
            expression = children[i].generateTree(expression);
        
        return expression;
    }
    
    public double calculate(int sample)
    {
        return function.calculate(sample, children);
    }
}