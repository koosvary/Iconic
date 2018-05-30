package org.iconic.ea.chromosome;

import org.iconic.ea.chromosome.expression.ExpressionChromosome;
import org.iconic.ea.chromosome.graph.FunctionNode;
import org.iconic.ea.chromosome.graph.InputNode;
import org.iconic.ea.chromosome.graph.Node;
import org.iconic.ea.operator.primitive.Addition;
import org.iconic.ea.operator.primitive.FunctionalPrimitive;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ExpressionChromosomeTest {
    @Test
    @DisplayName("Test that the genome of expression chromosomes are unique")
    void equalityTest() {
        List<Node<Double>> genome = generateExpression();

        ExpressionChromosome<Double> c1 = new ExpressionChromosome<>(3, 4, 3);
        ExpressionChromosome<Double> c2 = new ExpressionChromosome<>(3, 4, 3);

        c1.setGenome(genome);
        c2.setGenome(genome);

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

    private List<Node<Double>> generateExpression() {
        int headerLength = 3;
        int featureSize = 3;

        List<Node<Double>> expression = new LinkedList<>();

        for (int i = 0; i < headerLength; i++) {
            if (Math.random() > 0.5) {
                // Create a function
                FunctionalPrimitive<Double, Double> function = new Addition();
                expression.add(new FunctionNode<>(function));
            } else {
                // Feature Index
                int index = (int) Math.floor(Math.random() * (featureSize - 1));
                expression.add(new InputNode<>(index));
            }
        }

        // Tail
        for (int i = 0; i < headerLength + 1; i++) {
            int index = (int) Math.floor(Math.random() * (featureSize - 1));
            expression.add(new InputNode<>(index));
        }

        return expression;
    }
}
