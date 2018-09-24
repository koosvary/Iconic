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

import org.iconic.ea.operator.primitive.FunctionalPrimitive;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * <p>
 * A ChromosomeFactory is used for easily replicating chromosomes.
 * </p>
 *
 * @param <T> The type of chromosome to be constructed
 * @param <R> The type class of the data to pass through the chromosome
 */
public abstract class ChromosomeFactory<T extends Chromosome<R>, R> {
    protected final SortedMap<String, FunctionalPrimitive<R, R>> functionalPrimitives;
    private int maxArity;
    private AtomicInteger mapIndex = new AtomicInteger(0);

    protected ChromosomeFactory() {
        functionalPrimitives = new TreeMap<>();
        this.maxArity = 0;
    }

    /**
     * <p>Returns a new chromosome constructed according to the parameters of this factory</p>
     *
     * @return a chromosome constructed by the factory
     */
    public abstract T getChromosome();

    public List<FunctionalPrimitive<R, R>> getFunctionalPrimitives() {
        return new LinkedList<>(functionalPrimitives.values());
    }

    public FunctionalPrimitive<R, R> getFunction(int i) {
        return getFunctionalPrimitives().get(i);
    }

    public void addFunction(List<FunctionalPrimitive<R, R>> functions) {
        assert (functions.size() > 0);

        for(FunctionalPrimitive f : functions
        ){
            functionalPrimitives.put(f.getSymbol(),f);
        }
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
