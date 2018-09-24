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

import org.iconic.ea.chromosome.Chromosome;
import org.iconic.ea.operator.evolutionary.crossover.Crossover;
import org.iconic.ea.operator.evolutionary.mutation.Mutator;
import org.iconic.ea.operator.evolutionary.selection.Selection;
import org.iconic.ea.operator.objective.Objective;

import java.util.LinkedList;
import java.util.List;

public abstract class EvolutionaryAlgorithm<T extends Chromosome<R>, R> {
    private final List<Crossover<T, R>> crossovers;
    private final List<Mutator<T, R>> mutators;
    private final List<Selection<T, R>> selectors;
    private final List<Objective<R>> objectives;
    private double crossoverProbability;
    private double mutationProbability;
    private List<T> chromosomes;

    protected EvolutionaryAlgorithm() {
        this.crossovers = new LinkedList<>();
        this.mutators = new LinkedList<>();
        this.selectors = new LinkedList<>();
        this.objectives = new LinkedList<>();
        this.chromosomes = new LinkedList<>();
        this.crossoverProbability = 0.2;
        this.mutationProbability = 0.1;
    }

    public abstract void initialisePopulation(int populationSize);

    public abstract List<T> evolve(final List<T> population);

    protected List<Crossover<T, R>> getCrossovers() {
        return crossovers;
    }

    protected List<Mutator<T, R>> getMutators() {
        return mutators;
    }

    protected List<Selection<T, R>> getSelectors() {
        return selectors;
    }

    protected List<Objective<R>> getObjectives() {
        return objectives;
    }

    public Crossover<T, R> getCrossover(final int i) {
        return getCrossovers().get(i);
    }

    public Mutator<T, R> getMutator(final int i) {
        return getMutators().get(i);
    }

    public Selection<T, R> getSelector(final int i) {
        return getSelectors().get(i);
    }

    public Objective<R> getObjective(final int i) {
        return getObjectives().get(i);
    }

    public void addCrossover(Crossover<T, R> crossover) {
        getCrossovers().add(crossover);
    }

    public void addMutator(Mutator<T, R> mutator) {
        getMutators().add(mutator);
    }

    public void addSelector(Selection<T, R> selector) {
        getSelectors().add(selector);
    }

    public void addObjective(Objective<R> objective) {
        getObjectives().add(objective);
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
}
