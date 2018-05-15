package org.iconic.ea.operator.evolutionary.mutation.gep;

import org.iconic.ea.chromosome.Node;
import org.iconic.ea.chromosome.ExpressionChromosome;
import org.iconic.ea.operator.evolutionary.mutation.Mutator;
import org.iconic.ea.operator.primitive.FunctionalPrimitive;

import java.util.List;

public class ExpressionMutator<R> implements Mutator<ExpressionChromosome<R>, R> {

    @Override
    public ExpressionChromosome<R> apply(final List<FunctionalPrimitive<R, R>> functionalPrimitives,
                                         final ExpressionChromosome<R> chromosome) {
        // Pick an index of the chromosome to mutate
        int index = (int) Math.floor(Math.random() * chromosome.getExpressionLength());

        // Get all the functions available to use / replace with
        List<FunctionalPrimitive<R, R>> functions = functionalPrimitives;

        // Get the expression from the chromosome
        List<Node<R>> expression = chromosome.getExpression();


        // If the index is less than half way, pick from function or input variable. Otherwise only pick input variable
        if (index < Math.floor(chromosome.getExpressionLength() / 2)) {

            // Function and input variable
            if (Math.random() > 0.5) {
                // Create a function
                int functionIndex = (int) Math.floor(Math.random() * functions.size());
                FunctionalPrimitive<R, R> function = functionalPrimitives.get(functionIndex);
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
        ExpressionChromosome<R> newChromosome = new ExpressionChromosome<>();
        newChromosome.setExpression(expression);
        newChromosome.generateTree();

        return newChromosome;
    }
}