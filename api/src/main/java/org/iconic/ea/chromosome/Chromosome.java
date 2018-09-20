package org.iconic.ea.chromosome;

import org.iconic.ea.data.DataManager;
import org.iconic.ea.operator.primitive.FunctionalPrimitive;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * A chromosome is equivalent to an individual within a population, they're strongly typed and must return an
 * output of the same form as its input.
 * @param <T> The type class of the data to pass through the chromosome
 */
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
        /* if the input is only one term, return it */
        if(preorderExpression.split(" ").length==1){
            return preorderExpression;
        }
        if(topLevelFlag){
            if(preorderExpression.contains("Output")){
                preorderExpression = preorderExpression.substring(9);
            }
        }

        /* the functional primitive at the start of the input string */
        FunctionalPrimitive<T, T> leadingPrimitive=null;

        /* getting the string of the leading primitive by cutting the front off the input and trimming it */
        String firstFunction = preorderExpression.substring(0, preorderExpression.indexOf("(")).trim();

        /* match the firstFunction string to a functional primitive */
        leadingPrimitive = matchPrimitive(firstFunction, primitives);

        if(leadingPrimitive==null){
            return "Unrecognised function in expression";
        }

        /* this flag to determine whether or not this is the first time this method has been called, if it is, the
         * substring is slightly different. In both cases it cuts the function and its front and back parenthesis off
         */
        if(topLevelFlag){
            preorderExpression = preorderExpression.substring(leadingPrimitive.getSymbol().length() + 3, preorderExpression.length() - 4);
        }
        else{
            preorderExpression = preorderExpression.substring(leadingPrimitive.getSymbol().length() + 3, preorderExpression.length() - 2);
        }

        preorderExpression = preorderExpression.trim();

        /* if it's a unary operator, return a string that has the operator at the front and this method recursively
         * called on the rest of the expression
         */
        if(leadingPrimitive.getArity()==1){
            return leadingPrimitive.getSymbol() + "(" + getExpression(preorderExpression,primitives,false) + ")";
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
                    return "(" + getExpression(preorderExpression.substring(0, i-subValue), primitives, false) + ")"
                            + leadingPrimitive.getSymbol()
                            + "(" + getExpression(preorderExpression.substring(i + 2, preorderExpression.length()), primitives, false) + ")";
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

        /* this should only output on invalid expressions */
        return "Somethingwentwrong";
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

  
    /**
     * Returns the size of the chromosome.
     * @return The size of the chromosome
     */
    public abstract int getSize();
}
