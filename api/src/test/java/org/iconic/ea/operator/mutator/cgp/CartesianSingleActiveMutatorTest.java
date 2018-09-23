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
 * <p>A test suite for the {@link CartesianSingleActiveMutator} class</p>
 *
 * <p>This test suite ensures the following:
 * - when a chromosome is mutated it doesn't enter an invalid state</p>
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
