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
