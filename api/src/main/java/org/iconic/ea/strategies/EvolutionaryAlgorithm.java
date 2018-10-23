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
import org.iconic.ea.operator.evolutionary.crossover.Crossover;
import org.iconic.ea.operator.evolutionary.mutation.Mutator;
import org.iconic.ea.operator.evolutionary.selection.Selector;
import org.iconic.ea.operator.objective.CacheableObjective;
import org.iconic.ea.operator.objective.Objective;

import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

@Log4j2
public abstract class EvolutionaryAlgorithm<T extends Chromosome<R>, R extends Comparable<R>> {
    private final ChromosomeFactory<T, R> chromosomeFactory;
    private final List<Crossover<T, R>> crossovers;
    private final List<Mutator<T, R>> mutators;
    private final List<Selector<T>> selectors;
    private double crossoverProbability;
    private double mutationProbability;
    private Objective<R> objective;
    private List<T> chromosomes;

    protected EvolutionaryAlgorithm(ChromosomeFactory<T, R> chromosomeFactory) {
        this.chromosomeFactory = chromosomeFactory;
        this.crossovers = new LinkedList<>();
        this.mutators = new LinkedList<>();
        this.selectors = new LinkedList<>();
        this.chromosomes = new LinkedList<>();
        this.crossoverProbability = 0.2;
        this.mutationProbability = 0.1;
        this.objective = null;
    }

    public abstract void initialisePopulation(int populationSize);

    public abstract List<T> evolve(final List<T> population);

    /**
     * Elitism helps increase genetic diversity within a population without sacrificing fitness
     * by removing lower performing members of the population.
     *
     * @param population The population to enforce elitism on.
     * @return The same population of individuals that are provided.
     */
    protected List<T> elitism(List<T> population) {
        return new LinkedList<>(population);
    }

    protected List<Crossover<T, R>> getCrossovers() {
        return crossovers;
    }

    protected List<Mutator<T, R>> getMutators() {
        return mutators;
    }

    protected List<Selector<T>> getSelectors() {
        return selectors;
    }

    @SuppressWarnings("unchecked cast")
    public Objective<R> getObjective() {
        if (isCached(objective)) {
            CacheableObjective<R> cacheableObjective = (CacheableObjective<R>) getObjective();
            return cacheableObjective.getObjective();
        }

        return objective;
    }

    public Crossover<T, R> getCrossover(final int i) {
        return getCrossovers().get(i);
    }

    public Mutator<T, R> getMutator(final int i) {
        return getMutators().get(i);
    }

    public Selector<T> getSelector(final int i) {
        return getSelectors().get(i);
    }

    public void addCrossover(Crossover<T, R> crossover) {
        getCrossovers().add(crossover);
    }

    public void addMutator(Mutator<T, R> mutator) {
        getMutators().add(mutator);
    }

    public void addSelector(Selector<T> selector) {
        getSelectors().add(selector);
    }

    @SuppressWarnings("unchecked cast")
    public void setObjective(final Objective<R> objective) {
        if (isCached(objective)) {
            this.objective = new CacheableObjective<R>(objective);
        } else {
            this.objective = objective;
        }
    }

    private static boolean isCached(final Objective<?> objective) {
        try {
            Method m = EvolutionaryAlgorithm.class.getDeclaredMethod("setObjective", Objective.class);
            return false;
        } catch (NoSuchMethodException ex) {
            log.warn("Method setObjective(Objective) not found");
        }
        return false;
    }

    public List<T> getChromosomes() { return chromosomes; }

    public void setChromosomes(List<T> chromosomes) {
        this.chromosomes = chromosomes;
    }

    public double getCrossoverProbability() {
        return crossoverProbability;
    }

    public void setCrossoverProbability(double crossoverProbability) {
        this.crossoverProbability = crossoverProbability;
    }

    public double getMutationProbability() {
        return mutationProbability;
    }

    public void setMutationProbability(double mutationProbability) {
        this.mutationProbability = mutationProbability;
    }

    public ChromosomeFactory<T, R> getChromosomeFactory() {
        return chromosomeFactory;
    }
}
