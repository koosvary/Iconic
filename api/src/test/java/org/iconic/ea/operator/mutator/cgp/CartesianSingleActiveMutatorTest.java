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
package org.iconic.ea.operator.mutator.cgp;

import lombok.extern.log4j.Log4j2;
import org.iconic.ea.chromosome.cartesian.CartesianChromosome;
import org.iconic.ea.chromosome.cartesian.CartesianChromosomeFactory;
import org.iconic.ea.operator.evolutionary.mutation.Mutator;
import org.iconic.ea.operator.evolutionary.mutation.cgp.CartesianSingleActiveMutator;
import org.iconic.ea.operator.primitive.Addition;
import org.iconic.ea.operator.primitive.FunctionalPrimitive;
import org.iconic.ea.operator.primitive.Sin;
import org.iconic.ea.operator.primitive.Subtraction;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>A test suite for the {@link CartesianSingleActiveMutator} class
 *
 * <p>This test suite ensures the following:
 * - when a chromosome is mutated it doesn't enter an invalid state
 */
@Log4j2
@Disabled
public class CartesianSingleActiveMutatorTest {
    @RepeatedTest(10000)
    @DisplayName("Test that attempting to mutate a chromosome won't throw any exceptions")
    void mutationTest() {
        Mutator<CartesianChromosome<Double>, Double> mutator = new CartesianSingleActiveMutator<>();
        List<FunctionalPrimitive<Double, Double>> primitives = new ArrayList<>();
        primitives.add(new Addition());
        primitives.add(new Subtraction());
        primitives.add(new Sin());

        CartesianChromosomeFactory<Double> supplier = new CartesianChromosomeFactory<>(
            4, 10, 10, 10, 10
        );
        supplier.addFunction(primitives);

        final CartesianChromosome<Double> c = supplier.getChromosome();
        CartesianChromosome<Double> m = mutator.apply(primitives, c);
    }
}
