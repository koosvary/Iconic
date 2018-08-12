package org.iconic.ea.chromosome;

import org.iconic.ea.operator.primitive.FunctionalPrimitive;

import java.util.LinkedList;
import java.util.List;

/**
 * <p>
 * A ChromosomeFactory is used for easily replicating chromosomes.
 * </p>
 *
 * @param <T> The type of chromosome to be constructed
 * @param <R> The type class of the data to pass through the chromosome
 */
public abstract class ChromosomeFactory<T extends Chromosome<R>, R> {
    protected final List<FunctionalPrimitive<R, R>> functionalPrimitives;
    private int maxArity;

    protected ChromosomeFactory() {
        functionalPrimitives = new LinkedList<>();
        this.maxArity = 0;
    }

    /**
     * <p>Returns a new chromosome constructed according to the parameters of this factory</p>
     *
     * @return a chromosome constructed by the factory
     */
    public abstract T getChromosome();

    public List<FunctionalPrimitive<R, R>> getFunctionalPrimitives() {
        return functionalPrimitives;
    }

    public FunctionalPrimitive<R, R> getFunction(int i) {
        return getFunctionalPrimitives().get(i);
    }

    public void addFunction(List<FunctionalPrimitive<R, R>> functions) {
        assert (functions.size() > 0);

        getFunctionalPrimitives().addAll(functions);
    }

    /**
     * <p>Returns the maximum arity of the functions used by this factory</p>
     *
     * @return the maximum arity of the functions used by the factory
     */
    public int getMaxArity() {
        return maxArity;
    }

    /**
     *  <p>Sets the maximum arity of the functions used by this factory to the provided value</p>
     *
     * @param maxArity the new maximum of the functions used by the factory
     */
    protected void setMaxArity(int maxArity) {
        this.maxArity = maxArity;
    }
}
