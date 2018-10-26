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
package org.iconic.ea.operator.evolutionary.mutation;

import org.iconic.ea.chromosome.Chromosome;
import org.iconic.ea.operator.primitive.FunctionalPrimitive;

import java.util.List;

@FunctionalInterface
public interface Mutator<T extends Chromosome<R>, R> {
    /**
     * <p>Applies the mutation to a copy of the parent
     *
     * @param functionalPrimitives The list of functional primitives that this chromosome has available
     * @param chromosome           The parent chromosome which will be cloned then mutated to create the mutant
     * @return a mutated copy of the parent chromosome
     */
    T apply(final List<FunctionalPrimitive<R, R>> functionalPrimitives, final T chromosome);
}
