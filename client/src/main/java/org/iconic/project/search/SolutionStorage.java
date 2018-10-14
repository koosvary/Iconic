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
package org.iconic.project.search;

import com.sun.javafx.collections.ObservableMapWrapper;
import javafx.collections.ObservableMap;
import org.iconic.ea.chromosome.Chromosome;

import java.util.*;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class SolutionStorage<T extends Chromosome<?>> {

    private ObservableMap<Integer, List<T>> solutions = new ObservableMapWrapper<>(new HashMap<>());

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

            // Get the list of solutions with the same size as the current solution
            List<T> sameSizeSolutions = solutions.get(size);

            // For the first entry, create a new List
            if (sameSizeSolutions == null) {
                sameSizeSolutions = new ArrayList<>();
                sameSizeSolutions.add(0, solution);
                solutions.put(size, sameSizeSolutions);
                continue;
            }

            // Get the fitness of the current best solution of the same size
            T sameSizeSolutionBestFitness = sameSizeSolutions.get(sameSizeSolutions.size() - 1);
            double sameSizeBestFitness = sameSizeSolutionBestFitness.getFitness();

            // If the current solution has a better fitness then the best fitness with the same size, add it to the list
            if (fitness < sameSizeBestFitness) {
                sameSizeSolutions.add(0, solution);
            }
        }
    }

    /**
     * Get the HashMap containing the lists of solutions
     * @return HashMap containing the lists of solutions
     */
    public ObservableMap<Integer, List<T>> getSolutions() {
        return solutions;
    }
}
