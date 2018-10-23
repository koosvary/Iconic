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
package org.iconic.ea.strategies;

import lombok.extern.log4j.Log4j2;
import org.iconic.ea.chromosome.Chromosome;
import org.iconic.ea.chromosome.ChromosomeFactory;
import org.iconic.ea.operator.evolutionary.selection.RandomUniformSelector;
import org.iconic.ea.operator.evolutionary.selection.Selector;
import org.iconic.ea.operator.evolutionary.selection.SequentialSelector;
import org.iconic.ea.operator.objective.MultiObjective;
import org.iconic.ea.operator.objective.Objective;

import java.util.*;

/**
 * {@inheritDoc}
 * <p>
 * A simple evolutionary algorithm for multiple-objectives. Based on the paper written by
 * Christine L. Mumford-Valenzuela.
 *
 * @param <R> The type of chromosome used by the algorithm
 * @param <T> The type of data used by the chromosome
 */
@Log4j2
public abstract class MultiObjectiveEvolutionaryAlgorithm<R extends Chromosome<T>, T extends Comparable<T>>
        extends EvolutionaryAlgorithm<R, T> {
    private final Map<Objective<T>, Double> globals;
    private final Selector<R> defaultPrimarySelector;
    private final Selector<R> defaultSecondarySelector;
    private int lambda;

    /**
     * Constructs a new multi-objective evolutionary algorithm that uses the provided chromosome factory and a
     * λ of one. The λ used determines how many mutants are generated for each chromosome during
     * the mutation stage.
     *
     * @param chromosomeFactory The chromosome factory for the algorithm to use.
     */
    public MultiObjectiveEvolutionaryAlgorithm(ChromosomeFactory<R, T> chromosomeFactory) {
        this(chromosomeFactory, 1);
    }

    /**
     * Constructs a new multi-objective evolutionary algorithm that uses the provided chromosome factory and λ.
     * The λ used determines how many mutants are generated for each chromosome during
     * the mutation stage.
     *
     * @param chromosomeFactory The chromosome factory for the algorithm to use.
     * @param lambda            The number of mutants to generate per chromosome during the mutation phase.
     */
    public MultiObjectiveEvolutionaryAlgorithm(ChromosomeFactory<R, T> chromosomeFactory, int lambda) {
        super(chromosomeFactory);
        this.globals = new LinkedHashMap<>();
        this.defaultPrimarySelector = new SequentialSelector<>();
        this.defaultSecondarySelector = new RandomUniformSelector<>();
        this.lambda = lambda;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void initialisePopulation(int populationSize) {
        assert (populationSize > 0);
        assert (getObjective() instanceof MultiObjective);

        for (int i = 0; i < populationSize; i++) {
            final MultiObjective<T> objective = (MultiObjective<T>) getObjective();
            final R chromosome = getChromosomeFactory().getChromosome();
            getChromosomes().add(chromosome);

            objective.getGoals().forEach(goal ->
                    addGlobal(getGlobals(), goal, goal.apply(chromosome))
            );
            getObjective().apply(chromosome);
        }
    }

    /**
     * Replaces a parent with its offspring within the provided population given that the
     * offspring is not worse compared to the parent according to a specific objective.
     *
     * @param objective  The objective to compare against.
     * @param population The population to operate on.
     * @param parent     The parent to replace.
     * @param offspring  The offspring to replace the parent with.
     * @return Returns true if the parent was replaced.
     */
    protected boolean replaceParent(
            final Objective<T> objective,
            final List<R> population,
            final R parent,
            final R offspring
    ) {
        assert (objective instanceof MultiObjective);

        final MultiObjective<T> multiObjective = (MultiObjective<T>) objective;
        int i = population.indexOf(parent);

        if (i > -1) {
            population.set(i, offspring);
            multiObjective.getGoals().parallelStream()
                    .forEach(goal -> addGlobal(getGlobals(), goal, goal.apply(offspring)));
            multiObjective.apply(offspring);

            return true;
        }

        return false;
    }

    /**
     * Returns true if the provided chromosome is a global best.
     *
     * @param chromosome The chromosome to test.
     * @return True if the provided chromosome is a global best.
     */
    protected boolean isGlobalBest(final R chromosome) {
        return getGlobals().values().contains(chromosome.getFitness());
    }

    /**
     * Returns true if the first provided chromosome is dominated by the second chromosome
     * based on the given objective.
     *
     * @param objective The objective to compare against.
     * @param c1        The chromosome to test.
     * @param c2        The chromosome to test against.
     * @return Returns true if the first chromosome is dominated by the second chromosome.
     */
    protected boolean isDominatedBy(final Objective<T> objective, final R c1, final R c2) {
        assert (objective instanceof MultiObjective);

        MultiObjective<T> multiObjective = (MultiObjective<T>) objective;

        // If c2 performs worse for any goal it cannot dominate c1
        for (final Objective<T> goal : multiObjective.getGoals()) {
            if (!goal.isNotWorse(c2.getFitness(), c1.getFitness())) {
                return false;
            }
        }
        // Reaching here means that c2 must dominate c1
        return true;
    }

    /**
     * Returns true if the provided chromosome should be replaced and the replacement isn't
     * a duplicate of an existing chromosome in the given population.
     *
     * @param objective   The objective to use as the determining factor.
     * @param population  The population both chromosomes belong to.
     * @param chromosome  The chromosome to replace.
     * @param replacement The chromosome to use as the replacement.
     * @return True if the chromosome should be replaced.
     */
    protected boolean shouldReplace(
            final Objective<T> objective,
            final List<R> population,
            final R chromosome,
            final R replacement
    ) {
        if (population.contains(replacement)) {
            return false;
        }

        return objective.isNotWorse(replacement.getFitness(), chromosome.getFitness());
    }

    /**
     * @param c1
     * @param c2
     * @return A new chromosome that's a cross between both provided chromosomes.
     */
    protected R crossover(final R c1, final R c2) {
        // If no crossover operator is provided simply return the first chromosome unaltered
        if (getCrossovers().size() < 1) {
            return c1;
        }

        R child = getCrossover(0).apply(c1, c2);
        getObjective().apply(child);

        return child;
    }

    /**
     * Mutates the provided chromosome using a variant of the 1+λ strategy. This implementation
     * only replaces the parent if the mutant <i>is not worse</i>.
     *
     * @param chromosome The chromosome to mutate.
     * @return The mutant if it's not worse than the parent, otherwise the parent is returned.
     * @see <a href="https://en.wikipedia.org/wiki/Evolution_strategy">Evolutionary strategy - Wikipedia</a>
     */
    protected R mutate(final R chromosome) {
        assert (getMutators().size() > 0);
        Objects.requireNonNull(getObjective(), "An objective is required");

        // Compare chromosomes by their fitness using the ordering of the objective
        final Comparator<R> comparator =
                (o1, o2) -> getObjective()
                        .isNotWorse(o1.getFitness(), o2.getFitness())
                        // If chromosomes are equal just say it's better
                        ? -1 : 1;

        // Generate a pool of mutants
        List<R> children = new ArrayList<>(getLambda());

        // Create λ mutants
        for (int i = 0; i < getLambda(); ++i) {
            R child = getMutator(0).apply(
                    getChromosomeFactory().getFunctionalPrimitives(),
                    chromosome
            );
            getObjective().apply(child);
            children.add(child);
        }

        // Select the fittest mutant
        R bestChild = children
                .stream().min(comparator).get();

        // If they're equal to or better than the parent, replace the parent with the mutant
        return (getObjective().isNotWorse(bestChild.getFitness(), chromosome.getFitness()))
                ? bestChild
                : chromosome;
    }

    public Map<Objective<T>, Double> getGlobals() {
        return globals;
    }

    /**
     * Adds the provided fitness to the specified goal as a global best, given that it meets
     * all the criteria for being one.
     *
     * @param globals The map of globals to which the goal belongs to.
     * @param goal    The goal being tested against.
     * @param fitness The fitness to assign as a global best for the provided goal.
     */
    protected void addGlobal(
            final Map<Objective<T>, Double> globals,
            final Objective<T> goal,
            Double fitness
    ) {
        if (fitness.isNaN()) {
            log.warn("Attempting to add NaN to a global best");
            return;
        }

        // If the goal isn't present in the map of globals, add it, otherwise
        // check if the fitness is better than the value it's replacing
        if (!globals.containsKey(goal) || goal.isNotWorse(fitness, globals.get(goal))
        ) {
            globals.put(goal, fitness);
        }
    }

    @Override
    public Selector<R> getSelector(int i) {
        switch (i) {
            case 0:
                return (getSelectors().size() <= i || getSelector(i) == null)
                        ? defaultPrimarySelector
                        : getSelector(i);
            default:
                return (getSelectors().size() <= i || getSelector(i) == null)
                        ? defaultSecondarySelector
                        : getSelector(i);
        }
    }

    /**
     * Returns the number of mutants generated for each chromosome during the mutation stage.
     * The number of mutants generated is always at least one.
     *
     * @return The number of mutants generated for each chromosome during the mutation stage.
     */
    private int getLambda() {
        return (lambda < 1) ? 1 : lambda;
    }

    /**
     * Sets the number of mutants generated for each chromosome during the mutation stage.
     *
     * @param lambda The number of mutants to generate, must be greater than zero.
     */
    private void setLambda(int lambda) {
        // Set a minimum lambda of one
        this.lambda = (lambda < 1) ? 1 : lambda;
    }
}
