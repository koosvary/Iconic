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
package org.iconic.io.cli;

import com.beust.jcommander.Parameter;
import lombok.Getter;
import org.iconic.ea.EvolutionaryAlgorithmType;
import org.iconic.ea.operator.primitive.FunctionalPrimitive;

import java.util.List;

public class ArgsConverterFactory {
    @Getter
    @Parameter(names = {"--algorithm", "-a"}, description = "The type of evolutionary algorithm to use")
    private EvolutionaryAlgorithmType eaType;

    @Getter
    @Parameter(names = {"--input", "-i"}, required = true, description = "The dataset to use")
    private String input;

    @Getter
    @Parameter(names= {"--generations", "-g"}, required = true, description = "The number of generations to evolve")
    private int generations;

    @Getter
    @Parameter(names= {"--population", "-p"}, required = true, description = "The population size of the candidates")
    private int population;

    @Getter
    @Parameter(names= {"--crossoverProbability", "-cP"}, required = false, description = "The crossover probability for each candidate")
    private double crossoverProbability = 0.2;

    @Getter
    @Parameter(names= {"--mutationProbability", "-mP"}, required = false, description = "The mutation probability for each candidate")
    private double mutationProbability = 0.1;

    @Getter
    @Parameter(names= {"--outputs"}, required = true, description = "The number of outputs, if the chosen algorithm can't support multiple outputs this argument will be ignored")
    private int outputs = 1;

    @Getter
    @Parameter(names= {"--columns"}, description = "The number of columns if supported by the chosen algorithm")
    private int columns = 1;

    @Getter
    @Parameter(names= {"--rows"}, description = "The number of rows if supported by the chosen algorithm")
    private int rows = 1;

    @Getter
    @Parameter(names= {"--levelsBack"}, description = "The number of levels back if supported by the chosen algorithm")
    private int levelsBack = 1;

    @Getter
    @Parameter(names= {"--repeat", "-r"}, description = "The number of times to repeat the experiment. The results will be collated")
    private int repetitions = 1;

    @Getter
    @Parameter(names = {"--help", "-h"}, help = true)
    private boolean help;

    @Getter
    @Parameter(names = {"--graph"}, description = "Graph the solutions if true")
    private boolean graph;

    @Getter
    @Parameter(names = {"--csv"}, description = "Export the pareto-optimal front to a CSV file if true")
    private boolean csv;

    @Getter
    @Parameter(names = {"--primitives"}, description = "The primitives to use in the search",
     converter = PrimitiveTypeConverter.class)
    private List<FunctionalPrimitive<Double, Double>> primitives;

    @Getter
    @Parameter(names = {"--listPrimitives"}, help = true, description = "List all of the primitives that can be used in the search")
    private boolean listPrimitives;
}