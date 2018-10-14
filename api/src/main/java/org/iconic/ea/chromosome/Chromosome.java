/**
 * Copyright (C) 2018 Iconic
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.iconic.ea.chromosome;

import lombok.extern.log4j.Log4j2;
import org.iconic.ea.chromosome.cartesian.CartesianChromosome;
import org.iconic.ea.data.DataManager;
import org.iconic.ea.operator.primitive.FunctionalPrimitive;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A chromosome is equivalent to an individual within a population, they're strongly typed and must return an
 * output of the same form as its input.
 * @param <T> The type class of the data to pass through the chromosome
 */
@Log4j2
public abstract class Chromosome<T> {
    private boolean changed;
    private double fitness;
    private final int numFeatures;

    /**
     * Constructs a new chromosome with the specified number of features.
     * @param numFeatures The maximum number of features for the chromosome to express
     */
    public Chromosome(final int numFeatures) {
        this.changed = true;
        this.numFeatures = numFeatures;
    }

    /**
     * Sets the fitness of the chromosome to the specified value
     * @param fitness The new fitness of the chromosome
     */
    public void setFitness(final double fitness) {
        this.fitness = fitness;
    }

    /**
     * Returns the fitness of this chromosome.
     * @return the fitness of the chromosome
     */
    public double getFitness() {
        return fitness;
    }

    /**
     * <p>
     * Returns true if this chromosome has been changed.
     * </p>
     *
     * <p>
     * The changed flag is used to determine whether or not a chromosome needs to be re-evaluated. If the
     * flag isn't set to true after modifying the chromosome's genotype then its phenotype won't be updated to
     * reflect the changes.
     * </p>
     *
     * @return true if the chromosome has been changed
     */
    public boolean isChanged() {
        return changed;
    }

    /**
     * Sets the changed status of this chromosome to the specified value.
     * @param changed The new changed value
     * @see Chromosome#isChanged()
     */
    public void setChanged(boolean changed) {
        this.changed = changed;
    }

    /**
     * <p>Evaluates the specified input samples and returns a list of output values for each sample.</p>
     * <p>The samples used should be a two-dimensional matrix, with each sample on a separate row. The final
     * column needs to contain the expected result for its corresponding row.</p>
     * @param input The input samples to evaluate
     * @return A list of outputs, one for each input sample, referenced by output id
     */
    public abstract List<Map<Integer, T>> evaluate(final DataManager<T> input);

    /**
     * Returns the number of features this chromosome can express.
     * @return the number of features the chromosome can express
     */
    public int getInputs() {
        return numFeatures;
    }

    /**
     * <p>
     * Takes a pre-order expression and recursively converts it to a mathematical expression
     * </p>
     * @param preorderExpression The expression that is being processed
     * @param primitives The list of primitives currently available to the chromosome
     * @param topLevelFlag Used to determine whether or not it's the first call of the function
     * @return the processed expression string
     */

    public String getExpression(String preorderExpression, List<FunctionalPrimitive<T, T>> primitives, boolean topLevelFlag){
        HashMap<String, String> symbolMap = getSymbolMap();
        /* if the input is only one term, return it */
        if(preorderExpression.split(" ").length==1){
            return preorderExpression;
        }

        String[] expressionSplit = preorderExpression.split("\nOutput = ");
        if(expressionSplit.length > 2){
            String output = "";
            for(int i = 1; i < expressionSplit.length; i++){
                if(i == expressionSplit.length-1)
                    output += getExpression(expressionSplit[i], primitives, true);
                else
                    output += getExpression(expressionSplit[i], primitives, true) + "\ny"+ (i+1) +" = ";
            }
            return output;
        }

        /* I don't remember adding this if block */
        if(topLevelFlag){
            if(preorderExpression.contains("Output")){
                preorderExpression = preorderExpression.substring(10);
                return getExpression(preorderExpression, primitives, true);
            }
        }

        /* the functional primitive at the start of the input string */
        FunctionalPrimitive<T, T> leadingPrimitive=null;

        /* getting the string of the leading primitive by cutting the front off the input and trimming it */
        if(preorderExpression.indexOf("(") == -1){
            return "";
        }
        String firstFunction = preorderExpression.substring(0, preorderExpression.indexOf("(")).trim();

        /* match the firstFunction string to a functional primitive */
        leadingPrimitive = matchPrimitive(firstFunction, primitives);

        if(leadingPrimitive==null){
            log.warn("Unrecognised function in expression");
            return "Unrecognised function in expression";
        }

        /* this flag to determine whether or not this is the first time this method has been called, if it is, the
         * substring is slightly different. In both cases it cuts the function and its front and back parentheses off
         */

        if(topLevelFlag){
            if(preorderExpression.endsWith("  ) "))
                preorderExpression = preorderExpression.substring(leadingPrimitive.getSymbol().length() + 3, preorderExpression.length() - 4);
            else
                preorderExpression = preorderExpression.substring(leadingPrimitive.getSymbol().length() + 3, preorderExpression.length() - 3);
        }
        else{
            preorderExpression = preorderExpression.substring(leadingPrimitive.getSymbol().length() + 3, preorderExpression.length() - 2);
        }

        preorderExpression = preorderExpression.trim();

        /* if it's a unary operator, return a string that has the operator at the front and this method recursively
         * called on the rest of the expression
         */
        if(leadingPrimitive.getArity()==1){
            return symbolMap.containsKey(leadingPrimitive.getSymbol()) ? symbolMap.get(leadingPrimitive.getSymbol()) +
                    "(" + getExpression(preorderExpression,primitives,false) + ")" : leadingPrimitive.getSymbol() +
                    "(" + getExpression(preorderExpression,primitives,false) + ")";
        }

        /* if it's a binary operator we need to find its two inputs in the expression string
         * this is achieved by checking if the current number of open parentheses is equal to 0 and then finding the comma
         */
        else if(leadingPrimitive.getArity()==2){

            /* to keep track of the number of open parentheses */
            int parenCount = 0;

            /* loops through the expression string until it finds a comma when open parentheses is 0
             * if it is zero it means we aren't inside any of the functions that comes after the open paren
             * of this function - it makes sense, trust me
             */
            for(int i = 0; i < preorderExpression.length(); i++){
                if(preorderExpression.charAt(i) == ',' && parenCount == 0){

                    /* this value is used to store the number of leading spaces in front of the comma */
                    int subValue = 0;
                    if((i > 1) && (preorderExpression.charAt(i - 2) == ' ') && (preorderExpression.charAt(i - 1) == ' ')){
                        subValue = 2;
                    }
                    else if(preorderExpression.charAt(i-1) == ' '){
                        subValue = 1;
                    }

                    /* return this method called on the left input to the function
                     * + the function string
                     * + this method called on the right input to the function*/
                    if(!leadingPrimitive.getSymbol().equals("MAX") && !leadingPrimitive.getSymbol().equals("MIN")){
                        return "(" + getExpression(preorderExpression.substring(0, i - subValue), primitives, false) + ")"
                                + (symbolMap.containsKey(leadingPrimitive.getSymbol()) ? symbolMap.get(leadingPrimitive.getSymbol()) : leadingPrimitive.getSymbol())
                                + "(" + getExpression(preorderExpression.substring(i + 2, preorderExpression.length()), primitives, false) + ")";
                    }
                    else{
                        return (symbolMap.containsKey(leadingPrimitive.getSymbol()) ? symbolMap.get(leadingPrimitive.
                                getSymbol()) : leadingPrimitive.getSymbol()) + "((" + getExpression(preorderExpression.
                                substring(0, i - subValue), primitives, false) + "),(" +
                                getExpression(preorderExpression.substring(i + 2, preorderExpression.length()), primitives,false) + "))";
                    }
                }

                /* just counting and un-counting parens */
                if(preorderExpression.charAt(i) == '('){
                    parenCount++;
                }
                else if(preorderExpression.charAt(i) == ')'){
                    parenCount--;
                }
            }
        }
        else if(leadingPrimitive.getSymbol().equals("IF")){
            /* to keep track of the number of open parentheses */
            int parenCount = 0;
            /* loops through the expression string until it finds a comma when open parentheses is 0
             * if it is zero it means we aren't inside any of the functions that comes after the open paren
             * of this function - it makes sense, trust me
             */
            int firstCommaPosition = 0, secondCommaPosition = 0;
            for(int i = 0; i < preorderExpression.length(); i++){
                if(preorderExpression.charAt(i) == ',' && parenCount == 0){
                    if(firstCommaPosition == 0){
                        firstCommaPosition = i;
                    }
                    else{
                        secondCommaPosition = i;
                    }
                }
                /* just counting and un-counting parens */
                if(preorderExpression.charAt(i) == '('){
                    parenCount++;
                }
                else if(preorderExpression.charAt(i) == ')'){
                    parenCount--;
                }
            }

            /* this value is used to store the number of leading spaces in front of the comma */
            int subValueOne = 0, subValueTwo = 0;
            if((firstCommaPosition > 1) && (preorderExpression.charAt(firstCommaPosition - 2) == ' ') && (preorderExpression.charAt(firstCommaPosition - 1) == ' ')){
                subValueOne = 2;
            }
            else if(preorderExpression.charAt(firstCommaPosition-1) == ' '){
                subValueOne = 1;
            }
            if((secondCommaPosition > 1) && (preorderExpression.charAt(secondCommaPosition - 2) == ' ') && (preorderExpression.charAt(secondCommaPosition - 1) == ' ')){
                subValueTwo = 2;
            }
            else if(preorderExpression.charAt(secondCommaPosition-1) == ' '){
                subValueTwo = 1;
            }

            String result = "";
            /* return this method called on the left input to the function
             * + the function string
             * + this method called on the right input to the function*/

            return "IF((" + getExpression(preorderExpression.substring(0, firstCommaPosition - subValueOne), primitives, false) + ") < 0) : "
                + "(" + getExpression(preorderExpression.substring(firstCommaPosition + 2, secondCommaPosition - subValueTwo), primitives, false) + ") ELSE : ("
                + "(" + getExpression(preorderExpression.substring(secondCommaPosition + 2, preorderExpression.length()), primitives, false) + "))";
        }

        /* this should only output on invalid expressions */
        log.warn("Unable to parse expression");
        return "Something went wrong";
    }

    private HashMap<String, String> getSymbolMap(){
        HashMap<String, String> output = new HashMap<>();

        output.put("ADD", "+");
        output.put("ACOS", "ARCCOS");
        output.put("ASIN", "ARCSIN");
        output.put("ATAN", "ARCTAN");
        output.put("CEIL", "CEILING");
        output.put("COS", "COS");
        output.put("DIV", "/");
        output.put("EQUAL", "EQUALS");
        output.put("MUL", "*");
        output.put("POW", "^");
        output.put("SGN", "SIGN");
        output.put("SUB", "-");

        return output;
    }

    /* this loops through all of the primitives in the input primitive list to match the text of the input primitive
     * string and return the matched primitive
     */
    private FunctionalPrimitive<T, T> matchPrimitive(String primString, List<FunctionalPrimitive<T, T>> primitives){

        FunctionalPrimitive<T, T> tempPrim = null;
        for(FunctionalPrimitive f :
                primitives){
            if(f.getSymbol().toLowerCase().equals(primString.toLowerCase())){
                tempPrim = f;
                break;
            }
        }
        return tempPrim;
    }

    public String simplifyExpression(String expression){
        String coXPlusCoXPattern = "\\(([-?0-9]+)?\\*?([A-Za-z0-9_]+)\\)\\+\\(([-?0-9]+)?\\*?\\2\\)";
        String coXMinusCoXPattern = "\\(([-?0-9]+)?\\*?([A-Za-z0-9_]+)\\)\\-\\(([-?0-9]+)?\\*?\\2\\)";
        String coXTimesCoXPattern = "\\(([-?0-9]+)?\\*?([A-Za-z0-9_]+)\\)\\*\\(([-?0-9]+)?\\*?\\2\\)";
        String coXDividesCoXPattern = "\\(([-?0-9]+)?\\*?([A-Za-z0-9_]+)\\)\\/\\(([-?0-9]+)?\\*?\\2\\)";

        String anythingOpAnythingPattern = "\\(([A-Za-z0-9_]+)\\)([\\+\\-\\/\\*\\,\\<\\=\\>\\=])\\(([A-Za-z0-9_]+)\\)";
        String xTimesRootPattern = "\\(([A-Za-z0-9_]+)\\)ROOT";
//        String parenVarParenPattern = "\\(([A-Za-z0-9_]+)\\)";
//
//        Pattern pattern = Pattern.compile(parenVarParenPattern);
//        Matcher matcher = pattern.matcher(expression);
//
//        while(matcher.find()){
//            expression = expression.replaceFirst("\\(" + matcher.group(1) + "\\)", matcher.group(1));
//        }

        Pattern pattern = Pattern.compile(xTimesRootPattern);
        Matcher matcher = pattern.matcher(expression);

        while(matcher.find()){
            expression = expression.replaceFirst("\\(" + matcher.group(1) + "\\)ROOT", matcher.group(1) + "*ROOT");
        }

        pattern = Pattern.compile(anythingOpAnythingPattern);
        matcher = pattern.matcher(expression);

        while(matcher.find()){
            expression = expression.replaceFirst("\\(" + matcher.group(1) + "\\)", matcher.group(1));
            expression = expression.replaceFirst("\\(" + matcher.group(3) + "\\)", matcher.group(3));
        }

        pattern = Pattern.compile(coXPlusCoXPattern);
        matcher = pattern.matcher(expression);

        while(matcher.find()){
            int a = 1, b = 1;
            if(matcher.group(1) != null){
                a = Integer.parseInt(matcher.group(1));
            }
            if(matcher.group(3) != null){
                b = Integer.parseInt(matcher.group(3));
            }
            if(a+b == 0){
                expression = expression.replaceAll(coXPlusCoXPattern, "(0)");
            }
            else if(a+b == 1){
                expression = expression.replaceAll(coXPlusCoXPattern, "($2)");
            }
            else if(a+b == -1){
                expression = expression.replaceAll(coXPlusCoXPattern, "(-$2)");
            }
            else{
                expression = expression.replaceAll(coXPlusCoXPattern, "(" + (a+b) + "*$2)");
            }

        }
        pattern = Pattern.compile(coXMinusCoXPattern);
        matcher = pattern.matcher(expression);

        while(matcher.find()){
            int a = 1, b = 1;
            if(matcher.group(1) != null){
                a = Integer.parseInt(matcher.group(1));
            }
            if(matcher.group(3) != null){
                b = Integer.parseInt(matcher.group(3));
            }
            if(a-b == 0){
                expression = expression.replaceAll(coXMinusCoXPattern, "(0)");
            }
            else if(a-b == 1){
                expression = expression.replaceAll(coXMinusCoXPattern, "($2)");
            }
            else if(a-b == -1){
                expression = expression.replaceAll(coXMinusCoXPattern, "(-$2)");
            }
            else{
                expression = expression.replaceAll(coXMinusCoXPattern, "(" + (a - b) + "*$2)");
            }
        }
        pattern = Pattern.compile(coXTimesCoXPattern);
        matcher = pattern.matcher(expression);

        while(matcher.find()){
            int a = 1, b = 1;
            if(matcher.group(1) != null){
                a = Integer.parseInt(matcher.group(1));
            }
            if(matcher.group(3) != null){
                b = Integer.parseInt(matcher.group(3));
            }
            if(a*b == 1){
                expression = expression.replaceAll(coXTimesCoXPattern, "($2^2)");
            }
            else if(a*b == -1){
                expression = expression.replaceAll(coXTimesCoXPattern, "(-$2^2)");
            }
            else{
                expression = expression.replaceAll(coXTimesCoXPattern, "(" + (a * b) + "$2^2)");
            }
        }
        pattern = Pattern.compile(coXDividesCoXPattern);
        matcher = pattern.matcher(expression);

        while(matcher.find()){
            int a = 1, b = 1;
            if(matcher.group(1) != null){
                a = Integer.parseInt(matcher.group(1));
            }
            if(matcher.group(3) != null){
                b = Integer.parseInt(matcher.group(3));
            }
            if(a/b == 1){
                expression = expression.replaceAll(coXDividesCoXPattern, "(1)");
            }
            else if(a/b == -1){
                expression = expression.replaceAll(coXDividesCoXPattern, "(-1)");
            }
            else if((float)a/(float)b < 0){
                expression = expression.replaceAll(coXDividesCoXPattern, "(-(" + Math.abs(a) + "/" + Math.abs(b) + "))");
            }
            else if(a < 0 && b < 0){
                expression = expression.replaceAll(coXDividesCoXPattern, "(" + Math.abs(a) + "/" + Math.abs(b) + ")");
            }
            else{
                expression = expression.replaceAll(coXDividesCoXPattern, "(" + a + "/" + b + ")");
            }
        }
        return expression;
    }

    /**
     * Returns the size of the chromosome.
     * @return The size of the chromosome
     */
    public abstract int getSize();

    public abstract Chromosome<T> clone();
}
