package org.iconic.io;

import com.beust.jcommander.Parameter;
import org.iconic.ea.EvolutionaryAlgorithmType;

public class ArgsConverterFactory {
    @Parameter(names = {"--algorithm", "-a"}, description = "The type of evolutionary algorithm to use")
    private EvolutionaryAlgorithmType eaType;

    @Parameter(names = {"--input", "-i"}, required = true, description = "The dataset to use")
    private String input;

    @Parameter(names= {"--generations", "-g"}, required = true, description = "The number of generations to evolve")
    private int generations;

    @Parameter(names= {"--population", "-p"}, required = true, description = "The population size of the candidates")
    private int population;

    @Parameter(names = {"--help", "-h"}, help = true)
    private boolean help;

    public EvolutionaryAlgorithmType getEaType() {
        return eaType;
    }

    public String getInput() {
        return input;
    }

    public boolean isHelp() {
        return help;
    }

    public int getGenerations() {
        return generations;
    }

    public int getPopulation() {
        return population;
    }
}