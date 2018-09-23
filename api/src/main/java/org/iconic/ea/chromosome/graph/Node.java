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
     * </p>
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
