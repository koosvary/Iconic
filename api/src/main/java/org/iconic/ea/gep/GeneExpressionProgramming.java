package org.iconic.ea.gep;

import org.iconic.ea.EvolutionaryAlgorithm;
import org.iconic.ea.chromosome.Node;
import org.iconic.ea.chromosome.TreeChromosome;
import org.iconic.ea.data.DataManager;
import org.iconic.ea.operator.primitive.FunctionalPrimitive;

import java.util.LinkedList;
import java.util.List;

public class GeneExpressionProgramming<T> extends EvolutionaryAlgorithm<T, TreeChromosome<T>> {
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

        int featureSize = DataManager.getFeatureSize();
        List<FunctionalPrimitive<T>> functions = getFunctionalPrimitives();
        if (functions.size() == 0) {
            System.out.println("GeneExpressionProgramming  There are no Functions available");
            return null;
        }

        List<Node<T>> expression = new LinkedList<>();
        for (int i = 0; i < headerLength; i++) {
            if (Math.random() > 0.5) {
                // Create a function
                int index = (int) Math.floor(Math.random() * functions.size());
                FunctionalPrimitive function = functions.get(index);
                expression.add(new Node<T>(function));
            } else {
                // Feature Index
                int index = (int) Math.floor(Math.random() * featureSize);
                expression.add(new Node<T>(index));
            }
        }

        // Tail
        for (int i = 0; i < headerLength + 1; i++) {
            int index = (int) Math.floor(Math.random() * featureSize);
            expression.add(new Node<T>(index));
        }

        return expression;
    }

    @Override
    public List<TreeChromosome<T>> evolve(List<TreeChromosome<T>> population) {



        return null;
    }

    @Override
    public T evaluate(List<T> sampleRowValues) {
        return null;
    }

    public List<TreeChromosome<T>> getChromosomes() { return chromosomes; }
}
