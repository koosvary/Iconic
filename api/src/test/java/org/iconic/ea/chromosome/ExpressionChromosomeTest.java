package org.iconic.ea.chromosome;

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
    @DisplayName("Test that expression chromosomes are unique")
    void equalityTest() {
        List<Node<Double>> expression = generateExpression();

        ExpressionChromosome<Double> c1 = new ExpressionChromosome<>(3, 4, 3);
        ExpressionChromosome<Double> c2 = new ExpressionChromosome<>(3, 4, 3);

        c1.setExpression(expression);
        c2.setExpression(expression);

        c1.generateTree();
        c2.generateTree();

        // Chromosomes using the same expression should not have the same object references, but they
        // should still have the same semantics
        assertAll("expression",
                () -> assertEquals(c1.getExpressionLength(), c2.getExpressionLength()),
                () -> {
                    for (int i = 0; i < c1.getExpressionLength(); ++i) {
                        assertNotSame(c1.getExpression().get(i), c2.getExpression().get(i));
                    }
                },
                () -> assertNotSame(c1.getExpression(), c2.getExpression()),
                () -> assertLinesMatch(
                        Arrays.asList(c1.getExpression().toString()),
                        Arrays.asList(c2.getExpression().toString())
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
