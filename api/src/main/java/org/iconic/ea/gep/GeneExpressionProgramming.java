package org.iconic.ea.gep;

import org.iconic.ea.EvolutionaryAlgorithm;
import org.iconic.ea.chromosome.Node;
import org.iconic.ea.chromosome.TreeChromosome;
import org.iconic.ea.data.DataManager;
import org.iconic.ea.operator.primitive.FunctionalPrimitive;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class GeneExpressionProgramming<T> extends EvolutionaryAlgorithm<TreeChromosome<T>, T> {
    private List<TreeChromosome<T>> chromosomes = new LinkedList<>();

    public GeneExpressionProgramming() {
        super();
    }

    public void generateGenePool(int geneSize) {
        for (int i = 0; i < geneSize; i++) {
            TreeChromosome<T> chromosome = new TreeChromosome<>();
            chromosome.setExpression(generateExpression());
            chromosome.generateTree();
            chromosomes.add(chromosome);
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
                FunctionalPrimitive<T> function = getFunctionalPrimitives().get(index);
                expression.add(new Node<>(function));
            } else {
                // Feature Index
                int index = (int) Math.floor(Math.random() * (featureSize - 1));
                expression.add(new Node<T>(index));
            }
        }

        // Tail
        for (int i = 0; i < headerLength + 1; i++) {
            int index = (int) Math.floor(Math.random() * (featureSize - 1));
            expression.add(new Node<T>(index));
        }

        return expression;
    }

    @Override
    public List<TreeChromosome<T>> evolve(List<TreeChromosome<T>> population) {
        final double mutationChance = 0.2;

        for (TreeChromosome<T> c: population) {
            if (ThreadLocalRandom.current().nextDouble(0, 1) < mutationChance) {
                c = mutate(c);
            }
        }

        return population;
    }

    public TreeChromosome<T> mutate(TreeChromosome<T> chromosome) {
        assert(getMutators().size() > 0);
        assert(getObjectives().size() > 0);

        TreeChromosome<T> child = getMutator(0).apply(getFunctionalPrimitives(), chromosome);

        // Evaluate the fitness of both chromosomes
        double parentFitness = getObjective(0).apply(chromosome);
        double childFitness = getObjective(0).apply(child);

        // Return the new chromosome if it's objectively better or equivalent to its parent
        if (childFitness <= parentFitness) {
            return child;
        }

        return chromosome;
    }

//    @Override
//    public T evaluate(List<T> sampleRowValues) {
//        return null;
//    }

    public List<TreeChromosome<T>> getChromosomes() { return chromosomes; }

    public void setChromosomes(List<TreeChromosome<T>> chromosomes) {
        this.chromosomes = chromosomes;
    }
}
