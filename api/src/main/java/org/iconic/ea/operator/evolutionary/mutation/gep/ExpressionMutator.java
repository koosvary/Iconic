package org.iconic.ea.operator.evolutionary.mutation.gep;

import org.iconic.ea.chromosome.Node;
import org.iconic.ea.chromosome.TreeChromosome;
import org.iconic.ea.operator.evolutionary.mutation.Mutation;
import org.iconic.ea.operator.primitive.FunctionalPrimitive;

import java.util.List;

public class ExpressionMutator<R> implements Mutation<TreeChromosome<R>, R> {

    @Override
    public TreeChromosome<R> apply(final List<FunctionalPrimitive<R>> functionalPrimitives,
                                   final TreeChromosome<R> chromosome) {
        // Pick an index of the chromosome to mutate
        int index = (int) Math.floor(Math.random() * chromosome.getExpressionLength());

        // Get all the functions available to use / replace with
        List<FunctionalPrimitive<R>> functions = functionalPrimitives;

        // Get the expression from the chromosome
        List<Node<R>> expression = chromosome.getExpression();


        // If the index is less than half way, pick from function or input variable. Otherwise only pick input variable
        if (index < Math.floor(chromosome.getExpressionLength() / 2)) {

            // Function and input variable
            if (Math.random() > 0.5) {
                // Create a function
                int functionIndex = (int) Math.floor(Math.random() * functions.size());
                FunctionalPrimitive<R> function = functionalPrimitives.get(functionIndex);
                expression.set(index, new Node<R>(function));
            } else {
                // Feature Index
                int functionIndex = (int) Math.floor(Math.random() * functions.size());
                expression.set(index, new Node<R>(functionIndex));
            }

        } else { // Only Variable
            // Feature Index
            int functionIndex = (int) Math.floor(Math.random() * functions.size());
            expression.set(index, new Node<R>(functionIndex));
        }

        // Create the new Chromosome with the mutation
        TreeChromosome<R> newChromosome = new TreeChromosome<>();
        newChromosome.setExpression(expression);
        newChromosome.generateTree();

        return newChromosome;
    }
}