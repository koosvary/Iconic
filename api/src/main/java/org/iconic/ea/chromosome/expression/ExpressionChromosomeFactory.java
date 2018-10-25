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
package org.iconic.ea.chromosome.expression;

import org.iconic.ea.chromosome.ChromosomeFactory;
import org.iconic.ea.chromosome.graph.FunctionNode;
import org.iconic.ea.chromosome.graph.InputNode;
import org.iconic.ea.chromosome.graph.Node;
import org.iconic.ea.operator.primitive.Constant;
import org.iconic.ea.operator.primitive.FunctionalPrimitive;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * {@inheritDoc}
 *
 * <P>Chromosomes constructed by this factory form an expression tree.
 *
 * @param <T> The type class of the data to pass through the chromosome
 */
public class ExpressionChromosomeFactory<T> extends ChromosomeFactory<ExpressionChromosome<T>, T> {
    private final int headLength;
    private final int numFeatures;
    private int tailLength;
    private final Map<Integer, String> featureLabels;


    /**
     * <p>
     * Constructs a new expression chromosome factory that constructs expression chromosomes with the provided head
     * length, and number of features.
     *
     *
     * @param headLength  The length of the chromosome's head for chromosome's constructed by the factory
     * @param numFeatures The number of features that may be expressed by the chromosome's constructed by the factory
     */
    public ExpressionChromosomeFactory(int headLength, int numFeatures) {
        this(headLength, new ArrayList<>(), numFeatures);
    }

    /**
     * <p>
     * Constructs a new expression chromosome factory that constructs expression chromosomes with the provided head
     * length, and number of features.
     *
     *
     * @param headLength  The length of the chromosome's head for chromosome's constructed by the factory
     * @param numFeatures The number of features that may be expressed by the chromosome's constructed by the factory
     */
    public ExpressionChromosomeFactory(int headLength, List<String> inputs, int numFeatures) {
        super();

        assert (headLength > 0);
        assert (numFeatures > 0);

        this.headLength = headLength;
        this.numFeatures = numFeatures;
        this.tailLength = 0;
        this.featureLabels = new HashMap<>();
        for (int i = 0; i < inputs.size(); ++i) {
            featureLabels.put(i, inputs.get(i));
        }
    }

    /**
     * {@inheritDoc}
     */
    public ExpressionChromosome<T> getChromosome() {
        assert (getFunctionalPrimitives().size() > 0);

        ExpressionChromosome<T> chromosome = new ExpressionChromosome<>(
                getHeadLength(), getTailLength(), getNumFeatures(), getFeatureLabels()
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

    /**
     * <p>
     * Returns the head length of chromosomes constructed by this factory.
     *
     *
     * @return the head length of chromosomes constructed by the factory
     */
    public int getHeadLength() {
        return headLength;
    }

    /**
     * <p>
     * Returns the tail length of chromosomes constructed by this factory.
     *
     *
     * @return the tail length of chromosomes constructed by the factory
     */
    public int getTailLength() {
        return tailLength;
    }

    /**
     * <p>
     * Returns the number of features that chromosomes constructed by this factory can express.
     *
     *
     * @return the number of features that can be expressed by chromosomes constructed by the factory
     */
    public int getNumFeatures() {
        return numFeatures;
    }

    private void setTailLength(int tailLength) {
        this.tailLength = tailLength;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void addFunction(List<FunctionalPrimitive<T, T>> functions) {
        super.addFunction(functions);

        // Create a comparator for recalculating the maximum arity after we've added all the new functions
        final Comparator<FunctionalPrimitive<T, T>> comparator =
                Comparator.comparing(FunctionalPrimitive::getArity);

        // Find the new maximum arity
        final Optional<FunctionalPrimitive<T, T>> max = getFunctionalPrimitives().stream().max(comparator);

        if (max.isPresent()) {
            setMaxArity(max.get().getArity());
        } else { // If no max value is present something has gone horribly wrong (how'd it even get here?)
            throw new IllegalStateException(
                    "Invalid number of primitives present in chromosome factory." +
                    "There should be at least one primitive present.");
        }

        // We also need to recalculate the tail length
        setTailLength(headLength * (getMaxArity() - 1) + 1);
    }

    /**
     * <p>
     *
     *
     *
     * @param headLength  The length of the expression's head
     * @param tailLength  The length of the expression's tail
     * @param numFeatures The number of features that may be expressed
     * @param numFunctions The number of functions that are available
     * @return a genetic expression
     */
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
                expression.add(new InputNode<>(index, getFeatureLabels()));
            }
        }

        // Tail
        for (int i = 0; i < tailLength; i++) {
            final int index = ThreadLocalRandom.current().nextInt(numFeatures);
            if (Math.random() > p) {
                expression.add(new InputNode<>(index, getFeatureLabels()));
            } else {
                final double constant = ThreadLocalRandom.current().nextInt(10000) / 100.0;
                expression.add(new FunctionNode<>((Constant<T>) new Constant<>(constant)));
            }
        }

        return expression;
    }


    public Map<Integer, String> getFeatureLabels(){
        return featureLabels;
    }

}
