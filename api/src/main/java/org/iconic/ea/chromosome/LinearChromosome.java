package org.iconic.ea.chromosome;

import java.util.List;

/**
 * <p>
 * A linearly encoded chromosome. The user of this interface encodes their genotype in a compact linear format and
 * has direct access to the linear encoding of the genotype.
 * </p>
 *
 * @param <T> The type class of the data to pass through the chromosome
 */
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
