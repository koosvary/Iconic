package org.iconic.ea.chromosome;

import java.util.List;

public interface LinearChromosome<T> {
    /**
     * <p>
     * Returns the genome of this chromosome
     * </p>
     *
     * @return the genome of the chromosome
     */
    List<T> getGenome();
}
