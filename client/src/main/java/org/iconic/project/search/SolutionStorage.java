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
package org.iconic.project.search;

import com.sun.javafx.collections.ObservableMapWrapper;
import javafx.collections.ObservableMap;
import org.iconic.ea.chromosome.Chromosome;

import java.util.*;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class SolutionStorage<T extends Chromosome<?>> {

    private ObservableMap<Integer, T> solutions = new ObservableMapWrapper<>(new HashMap<>());

    /**
     * Given a list of new solutions, loop through the entire list and compare the fitness of each solution with the
     * current best solution of the same size. If the fitness is better, add it to the end of the list.
     * @param newSolutions A list of solutions to be compared
     */
    public void evaluate(final List<T> newSolutions) {
        // Loop through all new solutions
        for (T solution : newSolutions) {
            // Get the size and fitness of the current solution
            int size = solution.getSize();
            double fitness = solution.getFitness();

            // Get the old solution of the same size
            T oldSolution = solutions.get(size);

            // No value? Just set it
            if (oldSolution == null) {
                solutions.put(size, solution);
                continue;
            }

            // If the current solution has a better fitness then the best fitness with the same size, set it to the new one
            double sameSizeBestFitness = oldSolution.getFitness();
            if (fitness < sameSizeBestFitness) {
                solutions.put(size, solution);
            }
        }
    }

    /**
     * Get the HashMap containing the lists of solutions
     * @return HashMap containing the lists of solutions
     */
    public ObservableMap<Integer, T> getSolutions() {
        return solutions;
    }
}
