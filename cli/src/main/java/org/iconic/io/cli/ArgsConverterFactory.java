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
package org.iconic.io.cli;

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

    @Parameter(names= {"--crossoverProbability", "-cP"}, required = false, description = "The crossover probability for each candidate")
    private double crossoverProbability = 0.2;

    @Parameter(names= {"--mutationProbability", "-mP"}, required = false, description = "The mutation probability for each candidate")
    private double mutationProbability = 0.1;

    @Parameter(names= {"--outputs"}, required = true, description = "The number of outputs, if the chosen algorithm can't support multiple outputs this argument will be ignored")
    private int outputs = 1;

    @Parameter(names= {"--columns"}, description = "The number of columns if supported by the chosen algorithm")
    private int columns = 1;

    @Parameter(names= {"--rows"}, description = "The number of rows if supported by the chosen algorithm")
    private int rows = 1;

    @Parameter(names= {"--levelsBack"}, description = "The number of levels back if supported by the chosen algorithm")
    private int levelsBack = 1;

    @Parameter(names= {"--repeat", "-r"}, description = "The number of times to repeat the experiment. The results will be collated")
    private int repetitions = 1;

    @Parameter(names = {"--help", "-h"}, help = true)
    private boolean help;

    @Parameter(names = {"--graph"}, description = "Graph the solutions if true")
    private boolean graph;

    @Parameter(names = {"--csv"}, description = "Export the pareto-optimal front to a CSV file if true")
    private boolean csv;

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

    public double getMutationProbability() {
        return mutationProbability;
    }

    public double getCrossoverProbability() {
        return crossoverProbability;
    }

    public int getOutputs () {
        return outputs;
    }

    public int getColumns () {
        return columns;
    }

    public int getRows() {
        return rows;
    }

    public int getLevelsBack() {
        return levelsBack;
    }

    public boolean isGraph() {
        return graph;
    }

    public boolean isCsv() {
        return csv;
    }

    public int getRepetitions() {
        return repetitions;
    }
}