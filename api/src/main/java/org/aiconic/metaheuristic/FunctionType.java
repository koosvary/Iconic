package org.aiconic.metaheuristic;

public enum FunctionType
{
    // BLOCKNAME( Symbol Representation, Complexity)
    INPUTVARIABLE("VAR", 0), CONSTANT("CON", 0), INTEGERCONSTANT("ICON", 0), 
    ADDITION("+", 2), SUBTRACTION("-", 2), MULTIPLY("*", 2), DIVISION("/", 2), POW("^", 2), MIN("<", 2), MAX(">", 2),
    SQRT("SQRT", 1), EXP("E", 1), SIN("SIN", 1), COS("COS", 1), TAN("TAN", 1), ABS("ABS", 1), LOG("LOG", 1);
            
    private String symbol;
    private int children;
    private double value;
    private int variableReference;
        
    FunctionType(String symbol, int children)
    {
        this.symbol = symbol;
        this.children = children;
    }
    
    public String symbol() 
    {
        return symbol;
    }
    
    public int children()
    {
        return children;
    }
    
    public int getChildren(String symbol)
    {
        return FunctionType.valueOf(symbol).children();
    }
    
    public static FunctionType getFunctionType(String shortcode)
    {
        for (FunctionType ft : FunctionType.values())
        {
            if (ft.symbol().equals(shortcode))
                return ft;
        }
        return null;
    }
}