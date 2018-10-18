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
package org.iconic.ea.operator.evolutionary.mutation.gep;

import org.iconic.ea.chromosome.expression.ExpressionChromosome;
import org.iconic.ea.chromosome.graph.FunctionNode;
import org.iconic.ea.chromosome.graph.InputNode;
import org.iconic.ea.chromosome.graph.Node;
import org.iconic.ea.operator.evolutionary.mutation.Mutator;
import org.iconic.ea.operator.primitive.FunctionalPrimitive;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class ExpressionMutator<R> implements Mutator<ExpressionChromosome<R>, R> {

    /**
     * {@inheritDoc}
     */
    @Override
    public ExpressionChromosome<R> apply(final List<FunctionalPrimitive<R, R>> functionalPrimitives,
                                         final ExpressionChromosome<R> chromosome) {
        assert (chromosome.getGenome().size() > 0);

        // Pick an index of the chromosome to mutate
        final ExpressionChromosome<R> mutant = chromosome.clone();
        final int index = ThreadLocalRandom.current().nextInt(mutant.getGenome().size());
        final int numFunctions = functionalPrimitives.size();
        final int numFeatures = mutant.getInputs();
        final double p = 0.5;

        // Get the expression from the chromosome
        List<Node<R>> expression = mutant.getGenome().stream().map(Node::clone)
                .collect(Collectors.toList());

        // If the index is in the head, pick from a function or input variable
        if (index < mutant.getHeadLength()) {
            // Function and input variable
            if (Math.random() > p) {
                // Create a function
                final int functionIndex = ThreadLocalRandom.current().nextInt(numFunctions);

                FunctionalPrimitive<R, R> function = functionalPrimitives.get(functionIndex);
                expression.set(index, new FunctionNode<>(function));
            } else {
                expression.set(index, generateFeatureOrConstant(numFeatures, p));
            }
        }
        // Otherwise only pick an input variable or constant
        else {
            expression.set(index, generateFeatureOrConstant(numFeatures, p));
        }

        mutant.setGenome(expression);

        return mutant;
    }

    /**
     * <p>
     * Generates either an input variable node or a constant node.
     *
     *
     * @param numFeatures The number of features that may be used as an input variable
     * @param p           The probability of picking an input variable versus a constant
     */
    private Node<R> generateFeatureOrConstant(int numFeatures, double p) {
//        if (Math.random() > p) {
        final int index = ThreadLocalRandom.current().nextInt(numFeatures);
        return new InputNode<>(index);
//        } else {
//            final double constant = (Math.random() * 100);
//            expression.add(new FunctionNode<>((Constant<R>) new Constant<>(constant)));
//        }
    }

    @Override
    public String toString() {
        return "Simple Expression Mutation";
    }
}