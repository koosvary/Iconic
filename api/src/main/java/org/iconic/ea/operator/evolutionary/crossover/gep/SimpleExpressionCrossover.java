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

    @Override
    public String toString() {
        return "Simple Expression Crossover";
    }
}
