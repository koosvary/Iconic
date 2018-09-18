package org.iconic.project.search.config;

public enum EvolutionaryAlgorithmType {
    GENE_EXPRESSION_PROGRAMMING("Gene Expression Programming"),
    CARTESIAN_GENETIC_PROGRAMMING("Cartesian Genetic Programming");

    private final String name;

    EvolutionaryAlgorithmType(final String name) {
        this.name = name;
    }

    private String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }
}
