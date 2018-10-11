/**
 * Copyright (C) 2018 Iconic
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.iconic.ea;

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
    protected Objective<R> getObjective() {
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
