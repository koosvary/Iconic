package org.iconic.ea.operator.evolutionary.crossover.gep;

import org.iconic.ea.chromosome.expression.ExpressionChromosome;
import org.iconic.ea.chromosome.graph.Node;
import org.iconic.ea.operator.evolutionary.crossover.Crossover;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class SimpleExpressionCrossover<R> implements Crossover<ExpressionChromosome<R>, R> {
    @Override
    public ExpressionChromosome<R> apply(final ExpressionChromosome<R> c1,
                                         final ExpressionChromosome<R> c2) {
        // Pick an index of the chromosome as the crossover point
        final int index = ThreadLocalRandom.current().nextInt(c1.getGenome().size());
        List<Node<R>> left = null;
        List<Node<R>> right = null;

        // Randomly decide which half of each chromosome should be used
        if (ThreadLocalRandom.current().nextDouble(0, 1) < 0.5) {
            left = c1.getGenome().stream().map(Node::clone).limit(index + 1).collect(Collectors.toList());
            right = c2.getGenome().stream().map(Node::clone).skip(index + 1).collect(Collectors.toList());
        } else {
            left = c2.getGenome().stream().map(Node::clone).limit(index + 1).collect(Collectors.toList());
            right = c1.getGenome().stream().map(Node::clone).skip(index + 1).collect(Collectors.toList());
        }

        left.addAll(right);

        assert(left.size() == c1.getGenome().size());

        // Create the new Chromosome with the crossover point
        ExpressionChromosome<R> newChromosome = new ExpressionChromosome<>(
                c1.getHeadLength(), c1.getTailLength(), c1.getInputs()
        );

        newChromosome.setGenome(left);

        return newChromosome;
    }
}
