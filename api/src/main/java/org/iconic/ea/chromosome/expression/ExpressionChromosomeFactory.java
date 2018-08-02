package org.iconic.ea.chromosome.expression;

import org.iconic.ea.chromosome.ChromosomeFactory;
import org.iconic.ea.chromosome.graph.FunctionNode;
import org.iconic.ea.chromosome.graph.InputNode;
import org.iconic.ea.chromosome.graph.Node;
import org.iconic.ea.operator.primitive.FunctionalPrimitive;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class ExpressionChromosomeFactory<T> extends ChromosomeFactory<ExpressionChromosome<T>, T> {
    private final int headLength;
    private final int numFeatures;
    private int maxArity;
    private int tailLength;

    public ExpressionChromosomeFactory(int headLength, int numFeatures) {
        this.headLength = headLength;
        this.numFeatures = numFeatures;
        this.maxArity = 0;
        this.tailLength = 0;
    }

    public ExpressionChromosome<T> getChromosome() {
        ExpressionChromosome<T> chromosome = new ExpressionChromosome<>(
                getHeadLength(), getTailLength(), getNumFeatures()
        );

        chromosome.setGenome(
                generateExpression(
                        getHeadLength(),
                        getTailLength(),
                        getNumFeatures(),
                        getFunctionalPrimitives().size()
                )
        );

        return chromosome;
    }

    public int getHeadLength() {
        return headLength;
    }

    public int getTailLength() {
        return tailLength;
    }

    public int getNumFeatures() {
        return numFeatures;
    }

    public int getMaxArity() {
        return maxArity;
    }

    private void setMaxArity(int maxArity) {
        this.maxArity = maxArity;
    }

    private void setTailLength(int tailLength) {
        this.tailLength = tailLength;
    }

    @Override
    public void addFunction(FunctionalPrimitive<T, T> function) {
        final Comparator<FunctionalPrimitive<T, T>> comparator =
                Comparator.comparing(FunctionalPrimitive::getArity);

        functionalPrimitives.add(function);

        //noinspection ConstantConditions
        setMaxArity(
                getFunctionalPrimitives()
                        .stream()
                        .max(comparator).get()
                        .getArity()
        );

        setTailLength(headLength * (maxArity - 1) + 1);
    }

    public List<Node<T>> generateExpression(int headLength, int tailLength, int numFeatures, int numFunctions) {
        List<Node<T>> expression = new LinkedList<>();

        final double p = 0.5;

        assert (numFunctions > 0);

        for (int i = 0; i < headLength; i++) {
            if (ThreadLocalRandom.current().nextDouble(0, 1) <= p) {
                // Create a function
                final int index = ThreadLocalRandom.current().nextInt(numFunctions);
                FunctionalPrimitive<T, T> function = getFunction(index);
                expression.add(new FunctionNode<>(function));
            } else {
                // Feature Index
                final int index = ThreadLocalRandom.current().nextInt(numFeatures);
                expression.add(new InputNode<>(index));
            }
        }

        // Tail
        for (int i = 0; i < tailLength; i++) {
            final int index = ThreadLocalRandom.current().nextInt(numFeatures);
//            if (Math.random() > p) {
            expression.add(new InputNode<>(index));
//            } else {
//                final double constant = (Math.random() * 100);
//                expression.add(new FunctionNode<>((Constant<T>) new Constant<>(constant)));
//            }
        }

        return expression;
    }

}
