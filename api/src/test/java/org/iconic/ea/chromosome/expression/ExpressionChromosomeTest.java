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
package org.iconic.ea.chromosome.expression;

import org.iconic.ea.chromosome.graph.FunctionNode;
import org.iconic.ea.chromosome.graph.InputNode;
import org.iconic.ea.chromosome.graph.Node;
import org.iconic.ea.operator.primitive.Addition;
import org.iconic.ea.operator.primitive.FunctionalPrimitive;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * <p>
 * A test suite for the {@link ExpressionChromosome} class.
 *
 *
 * <p>
 * This test suite ensures the following:
 *  - chromosomes are always unique objects that don't share <b>any</b> references
 *
 */
public class ExpressionChromosomeTest {
    @ParameterizedTest
    @MethodSource("equalityTestProvider")
    @DisplayName("Test that the genome of expression chromosomes are unique")
    void equalityTest(final ExpressionChromosome<Double> c1, final ExpressionChromosome<Double> c2) {
        // Chromosomes with the same genome should not have the same object references, but they
        // should still have the same semantics
        assertAll("genome",
                () -> assertEquals(c1.getGenome().size(), c2.getGenome().size()),
                () -> {
                    for (int i = 0; i < c1.getGenome().size(); ++i) {
                        assertNotSame(c1.getGenome().get(i), c2.getGenome().get(i));
                    }
                },
                () -> assertNotSame(c1.getGenome(), c2.getGenome()),
                () -> assertLinesMatch(
                        Arrays.asList(c1.getGenome().toString()),
                        Arrays.asList(c2.getGenome().toString())
                )
        );
    }

    /**
     * <p>
     * Returns a stream of expression chromosome tuples, where each tuple contains two identical
     * expression chromosome constructed from a randomly generated expression.
     *
     *
     * <p>
     * The contents of the expression aren't important.
     *
     *
     * @return a stream of identical expression chromosomes
     */
    private static Stream<Arguments> equalityTestProvider() {
        int headerLength = ThreadLocalRandom.current().nextInt(100);
        int numFeatures = ThreadLocalRandom.current().nextInt(100);

        List<Node<Double>> expression = new LinkedList<>();

        for (int i = 0; i < headerLength; i++) {
            if (Math.random() > 0.5) {
                // Create a function
                FunctionalPrimitive<Double, Double> function = new Addition();
                expression.add(new FunctionNode<>(function));
            } else {
                // Feature Index
                int index = (int) Math.floor(Math.random() * (numFeatures - 1));
                expression.add(new InputNode<>(index));
            }
        }

        // Tail
        for (int i = 0; i < headerLength + 1; i++) {
            int index = (int) Math.floor(Math.random() * (numFeatures - 1));
            expression.add(new InputNode<>(index));
        }

        ExpressionChromosome<Double> c1 = new ExpressionChromosome<>(headerLength, headerLength, numFeatures);
        ExpressionChromosome<Double> c2 = new ExpressionChromosome<>(headerLength, headerLength, numFeatures);

        c1.setGenome(expression);
        c2.setGenome(expression);

        return Stream.of(Arguments.of(c1, c2));
    }
}
