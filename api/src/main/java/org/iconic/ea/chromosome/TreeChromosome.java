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
package org.iconic.ea.chromosome;

import org.iconic.ea.chromosome.graph.Node;

/**
 * <p>
 * A tree-backed chromosome. The user of this interface stores their genotype as a tree-like structure and has
 * access to the root of the tree.
 * </p>
 *
 * @param <T> The type class of the data to pass through the chromosome
 */
public interface TreeChromosome<T> {
    /**
     * <p>
     * Returns the root of this chromosome
     * </p>
     *
     * @return the root of the chromosome
     */
    Node<T> getRoot();

    /**
     * <p>
     * Sets the root of this chromosome to the specified value
     * </p>
     *
     * @param root The new root of the chromosome
     */
    void setRoot(final Node<T> root);
}
