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
package org.iconic.ea.operator.evolutionary.mutation;

import org.iconic.ea.chromosome.Chromosome;
import org.iconic.ea.operator.primitive.FunctionalPrimitive;

import java.util.List;

@FunctionalInterface
public interface Mutator<T extends Chromosome<R>, R> {
    /**
     * <p>Applies the mutation to a copy of the parent</p>
     *
     * @param functionalPrimitives The list of functional primitives that this chromosome has available
     * @param chromosome           The parent chromosome which will be cloned then mutated to create the mutant
     * @return a mutated copy of the parent chromosome
     */
    T apply(final List<FunctionalPrimitive<R, R>> functionalPrimitives, final T chromosome);
}
