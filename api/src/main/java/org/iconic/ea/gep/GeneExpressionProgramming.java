package org.iconic.ea.gep;

import lombok.extern.log4j.Log4j2;
import org.iconic.ea.EvolutionaryAlgorithm;
import org.iconic.ea.chromosome.Chromosome;
import org.iconic.ea.chromosome.expression.ExpressionChromosome;
import org.iconic.ea.chromosome.graph.FunctionNode;
import org.iconic.ea.chromosome.graph.InputNode;
import org.iconic.ea.chromosome.graph.Node;
import org.iconic.ea.operator.primitive.FunctionalPrimitive;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Log4j2
public class GeneExpressionProgramming<T> extends EvolutionaryAlgorithm<ExpressionChromosome<T>, T> {
    private int maxArity;
    private int tailLength;
    private final int headLength;

    public GeneExpressionProgramming() {
        super();
        this.maxArity = 0;
        this.headLength = 10;
        this.tailLength = headLength * (maxArity - 1) + 1;
    }

    public void initialisePopulation(int populationSize, int numFeatures) {
        for (int i = 0; i < populationSize; i++) {
            ExpressionChromosome<T> chromosome = new ExpressionChromosome<>(headLength, tailLength, numFeatures);
            chromosome.setGenome(generateExpression(numFeatures));
            getObjective(0).apply(chromosome);
            getChromosomes().add(chromosome);
        }
    }

    public List<Node<T>> generateExpression(int numFeatures) {
        final Comparator<FunctionalPrimitive<T, T>> comparator =
                Comparator.comparing(FunctionalPrimitive::getArity);
        this.maxArity = getFunctionalPrimitives().stream().max(comparator).get().getArity();
        this.tailLength = headLength * (maxArity - 1) + 1;

        List<Node<T>> expression = new LinkedList<>();

        final int numFunctions = getFunctionalPrimitives().size();
        final double p = 0.5;

        assert (numFunctions > 0);

        for (int i = 0; i < headLength; i++) {
            if (ThreadLocalRandom.current().nextDouble(0, 1) <= p) {
                // Create a function
                final int index = ThreadLocalRandom.current().nextInt(numFunctions);
                FunctionalPrimitive<T, T> function = getFunctionalPrimitives().get(index);
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

    @Override
    public List<ExpressionChromosome<T>> evolve(List<ExpressionChromosome<T>> population) {
        final double crossoverChance = getCrossoverProbability();
        final double mutationChance = getMutationProbability();

        final Comparator<Chromosome<T>> comparator = Comparator.comparing(Chromosome::getFitness);
        final ExpressionChromosome<T> bestCandidate = population
                .stream().min(comparator).get();

        for (int i = 0, populationSize = population.size(); i < populationSize; i++) {
            ExpressionChromosome<T> c = population.get(i);
            // Perform crossover
            if (ThreadLocalRandom.current().nextDouble(0, 1) <= crossoverChance) {
                if (!c.equals(bestCandidate)) {
                    population.set(i, crossover(bestCandidate, c));
                }
            }

            // Perform mutation
            if (ThreadLocalRandom.current().nextDouble(0, 1) <= mutationChance) {
                population.set(i, mutate(c));
            }
        }

        return population;
    }

    public ExpressionChromosome<T> crossover(ExpressionChromosome<T> c1, ExpressionChromosome<T> c2) {
        assert (getCrossovers().size() > 0);

        ExpressionChromosome<T> child = getCrossover(0).apply(c1, c2);
        getObjective(0).apply(child);

        return child;
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
