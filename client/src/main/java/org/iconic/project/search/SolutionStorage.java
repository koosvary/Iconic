package org.iconic.project.search;

import org.iconic.ea.chromosome.expression.ExpressionChromosome;

import java.util.*;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class SolutionStorage<T> {
    private HashMap<Integer, List<ExpressionChromosome<T>>> solutions = new HashMap<>();

    /**
     * Given a list of new solutions, loop through the entire list and compare the fitness of each solution with the
     * current best solution of the same size. If the fitness is better, add it to the end of the list.
     * @param newSolutions A list of solutions to be compared
     */
    public void evaluate(List<ExpressionChromosome<T>> newSolutions) {
        // Loop through all new solutions
        for (ExpressionChromosome<T> solution : newSolutions) {
            // Get the size and fitness of the current solution
            int size = solution.getSize();
            double fitness = solution.getFitness();

            // Get the list of solutions with the same size as the current solution
            List<ExpressionChromosome<T>> sameSizeSolutions = solutions.get(size);

            // For the first entry, create a new List
            if (sameSizeSolutions == null) {
                sameSizeSolutions = new ArrayList<>();
                sameSizeSolutions.add(0, solution);
                solutions.put(size, sameSizeSolutions);
                continue;
            }

            // Get the fitness of the current best solution of the same size
            ExpressionChromosome<T> sameSizeSolutionBestFitness = sameSizeSolutions.get(sameSizeSolutions.size() - 1);
            double sameSizeBestFitness = sameSizeSolutionBestFitness.getFitness();

            // If the current solution has a better fitness then the best fitness with the same size, add it to the list
            if (fitness < sameSizeBestFitness) {
                sameSizeSolutions.add(0, solution);
                //updateController(); // TODO This would be the best spot to update the results view
            }
        }
    }

    /**
     * Get the HashMap containing the lists of solutions
     * @return HashMap containing the lists of solutions
     */
    public HashMap<Integer, List<ExpressionChromosome<T>>> getSolutions() {
        return solutions;
    }
}
