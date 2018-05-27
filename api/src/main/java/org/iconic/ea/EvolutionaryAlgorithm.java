package org.iconic.ea;

import org.iconic.ea.chromosome.Chromosome;
import org.iconic.ea.operator.evolutionary.crossover.Crossover;
import org.iconic.ea.operator.evolutionary.mutation.Mutator;
import org.iconic.ea.operator.evolutionary.selection.Selection;
import org.iconic.ea.operator.objective.Objective;
import org.iconic.ea.operator.primitive.FunctionalPrimitive;

import java.util.LinkedList;
import java.util.List;

public abstract class EvolutionaryAlgorithm<T extends Chromosome<R>, R> {
    private final List<FunctionalPrimitive<R, R>> functionalPrimitives;
    private final List<Crossover<T, R>> crossovers;
    private final List<Mutator<T, R>> mutators;
    private final List<Selection<T, R>> selectors;
    private final List<Objective<T, R>> objectives;
    private double crossoverProbability;
    private double mutationProbability;
    private List<T> chromosomes;

    protected EvolutionaryAlgorithm() {
        this.functionalPrimitives = new LinkedList<>();
        this.crossovers = new LinkedList<>();
        this.mutators = new LinkedList<>();
        this.selectors = new LinkedList<>();
        this.objectives = new LinkedList<>();
        this.chromosomes = new LinkedList<>();
        this.crossoverProbability = 0.2;
        this.mutationProbability = 0.1;
    }

    public abstract void initialisePopulation(int populationSize, int numFeatures);

    public abstract List<T> evolve(final List<T> population);

    protected List<FunctionalPrimitive<R, R>> getFunctionalPrimitives() {
        return functionalPrimitives;
    }

    protected List<Crossover<T, R>> getCrossovers() {
        return crossovers;
    }

    protected List<Mutator<T, R>> getMutators() {
        return mutators;
    }

    protected List<Selection<T, R>> getSelectors() {
        return selectors;
    }

    protected List<Objective<T, R>> getObjectives() {
        return objectives;
    }

    public FunctionalPrimitive<R, R> getFunction(final int i) {
        return getFunctionalPrimitives().get(i);
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

    public Objective<T, R> getObjective(final int i) {
        return getObjectives().get(i);
    }

    public void addFunction(FunctionalPrimitive<R, R> function) {
        getFunctionalPrimitives().add(function);
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

    public void addObjective(Objective<T, R> objective) {
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
