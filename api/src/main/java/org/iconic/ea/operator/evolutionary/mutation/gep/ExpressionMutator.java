package org.iconic.ea.operator.evolutionary.mutation.gep;

import org.iconic.ea.chromosome.ExpressionChromosome;
import org.iconic.ea.chromosome.FunctionNode;
import org.iconic.ea.chromosome.InputNode;
import org.iconic.ea.chromosome.Node;
import org.iconic.ea.operator.evolutionary.mutation.Mutator;
import org.iconic.ea.operator.primitive.FunctionalPrimitive;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class ExpressionMutator<R> implements Mutator<ExpressionChromosome<R>, R> {

    @Override
    public ExpressionChromosome<R> apply(final List<FunctionalPrimitive<R, R>> functionalPrimitives,
                                         final ExpressionChromosome<R> chromosome) {
        assert (chromosome.getExpressionLength() > 0);

        // Pick an index of the chromosome to mutate
        final int index = ThreadLocalRandom.current().nextInt(chromosome.getExpressionLength());
        final int numFunctions = functionalPrimitives.size();
        final int numFeatures = chromosome.getNumFeatures();
        final double p = 0.5;

        // Get the expression from the chromosome
        List<Node<R>> expression = chromosome.getExpression().stream().map(Node::clone)
                .collect(Collectors.toList());

        // If the index is in the head, pick from a function or input variable
        if (index < chromosome.getHeadLength()) {
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

        // Create the new Chromosome with the mutation
        ExpressionChromosome<R> newChromosome = new ExpressionChromosome<>(
                chromosome.getHeadLength(), chromosome.getTailLength(), chromosome.getNumFeatures()
        );

        newChromosome.setExpression(expression);
        newChromosome.generateTree();

        return newChromosome;
    }

    /**
     * <p>
     * Generates either an input variable node or a constant node.
     * </p>
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
}