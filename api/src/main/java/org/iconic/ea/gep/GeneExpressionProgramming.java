package org.iconic.ea.gep;

import lombok.extern.log4j.Log4j2;
import org.iconic.ea.EvolutionaryAlgorithm;
import org.iconic.ea.chromosome.Chromosome;
import org.iconic.ea.chromosome.expression.ExpressionChromosome;
import org.iconic.ea.chromosome.expression.ExpressionChromosomeFactory;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Log4j2
public class GeneExpressionProgramming<T> extends EvolutionaryAlgorithm<ExpressionChromosome<T>, T> {
    private final ExpressionChromosomeFactory<T> chromosomeFactory;

    public GeneExpressionProgramming(ExpressionChromosomeFactory<T> chromosomeFactory) {
        super();
        this.chromosomeFactory = chromosomeFactory;
    }

    public void initialisePopulation(int populationSize, int numFeatures) {
        for (int i = 0; i < populationSize; i++) {
            Chromosome<T> chromosome = getChromosomeFactory().getChromosome();
            getObjective(0).apply(chromosome);
            getChromosomes().add((ExpressionChromosome<T>) chromosome);
        }
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

        ExpressionChromosome<T> child = getMutator(0).apply(
                getChromosomeFactory().getFunctionalPrimitives(),
                chromosome
        );

        // Evaluate the fitness of both chromosomes
        double parentFitness = getObjective(0).apply(chromosome);
        double childFitness = getObjective(0).apply(child);

        // Return the new chromosome if it's objectively better or equivalent to its parent
        if (childFitness <= parentFitness) {
            return child;
        }

        return chromosome;
    }

    public ExpressionChromosomeFactory<T> getChromosomeFactory() {
        return chromosomeFactory;
    }
}
