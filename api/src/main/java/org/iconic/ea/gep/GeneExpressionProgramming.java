package org.iconic.ea.gep;

import lombok.extern.log4j.Log4j2;
import org.iconic.ea.EvolutionaryAlgorithm;
import org.iconic.ea.chromosome.ExpressionChromosome;
import org.iconic.ea.chromosome.FunctionNode;
import org.iconic.ea.chromosome.InputNode;
import org.iconic.ea.chromosome.Node;
import org.iconic.ea.data.DataManager;
import org.iconic.ea.operator.primitive.FunctionalPrimitive;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Log4j2
public class GeneExpressionProgramming<T> extends EvolutionaryAlgorithm<ExpressionChromosome<T>, T> {
    public GeneExpressionProgramming() {
        super();
    }

    public void initialisePopulation(int geneSize) {
        for (int i = 0; i < geneSize; i++) {
            ExpressionChromosome<T> chromosome = new ExpressionChromosome<>();
            chromosome.setExpression(generateExpression());
            chromosome.generateTree();
            getChromosomes().add(chromosome);
        }
    }

    public List<Node<T>> generateExpression() {
        int headerLength = 3;
        int numFunctions = getFunctionalPrimitives().size();

        int featureSize = DataManager.getFeatureSize();
        assert (numFunctions > 0);

        List<Node<T>> expression = new LinkedList<>();

        for (int i = 0; i < headerLength; i++) {
            if (Math.random() > 0.5) {
                // Create a function
                int index = (int) Math.floor(Math.random() * numFunctions);
                FunctionalPrimitive<T, T> function = getFunctionalPrimitives().get(index);
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

    @Override
    public List<ExpressionChromosome<T>> evolve(List<ExpressionChromosome<T>> population) {
        final double mutationChance = 1.0;

        for (ExpressionChromosome<T> c: population) {
            if (ThreadLocalRandom.current().nextDouble(0, 1) < mutationChance) {
                c = mutate(c);
                c.generateTree();
            }
        }

        return population;
    }

    public ExpressionChromosome<T> mutate(ExpressionChromosome<T> chromosome) {
        assert (getMutators().size() > 0);
        assert (getObjectives().size() > 0);

        ExpressionChromosome<T> child = getMutator(0).apply(getFunctionalPrimitives(), chromosome);

        // Evaluate the fitness of both chromosomes
        double parentFitness = getObjective(0).apply(chromosome);
        double childFitness = getObjective(0).apply(child);


        // Return the new chromosome if it's objectively better or equivalent to its parent
        if (childFitness <= parentFitness) {
            return child;
        }

        return chromosome;
    }
}
