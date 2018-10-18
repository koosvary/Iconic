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
package org.iconic.ea.chromosome.graph;
import java.util.LinkedList;
import java.util.List;

public abstract class Node<T> implements Cloneable {
    private List<Node<T>> children;

    public Node() {
        this.children = new LinkedList<>();
    }

    public void addChild(Node<T> n) {
        children.add(n);
    }

    public void addChildren(List<Node<T>> children) {
        this.children.addAll(children);
    }

    public void removeAllChildren() { children = new LinkedList<>(); }

    protected List<Node<T>> getChildren() {
        return children;
    }

    public abstract T apply(List<T> sampleRowValues);

    public abstract int getNumberOfChildren();

    public abstract Node<T> clone();

    /**
     * <p>
     * Recursively go through all children to determine the size of the Solution.
     *
     * @return the size of the solution.
     */
    public int getSize() {
        if (children.size() == 0) { return 1; }

        int sum = 1;
        for (Node<T> child : children) {
            sum += child.getSize();
        }

        return sum;
    }
}
