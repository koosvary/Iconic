/**
 * Copyright 2018 Iconic
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.iconic.ea.chromosome;

import org.iconic.ea.operator.primitive.FunctionalPrimitive;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * <p>
 * A ChromosomeFactory is used for easily replicating chromosomes.
 *
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
     * <p>Returns a new chromosome constructed according to the parameters of this factory
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
     * <p>Returns the maximum arity of the functions used by this factory
     *
     * @return the maximum arity of the functions used by the factory
     */
    public int getMaxArity() {
        return maxArity;
    }

    /**
     *  <p>Sets the maximum arity of the functions used by this factory to the provided value
     *
     * @param maxArity the new maximum of the functions used by the factory
     */
    protected void setMaxArity(int maxArity) {
        this.maxArity = maxArity;
    }
}
