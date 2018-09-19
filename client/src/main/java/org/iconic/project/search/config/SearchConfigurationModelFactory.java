package org.iconic.project.search.config;

public class SearchConfigurationModelFactory  {
    public SearchConfigurationModelFactory() {
        // Do nothing
    }

    public SearchConfigurationModel getSearchConfigurationModel(String name, EvolutionaryAlgorithmType type) {
        switch (type) {
            case GENE_EXPRESSION_PROGRAMMING: return new GepConfigurationModel(name);
            case CARTESIAN_GENETIC_PROGRAMMING: return new CgpConfigurationModel(name);
            default: return new GepConfigurationModel(name);
        }
    }
}
