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
import org.iconic.ea.operator.primitive.FunctionalPrimitive;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class FunctionNode<T> extends Node<T> {
    private  final FunctionalPrimitive<T, T> lambda;

    public FunctionNode(FunctionalPrimitive<T, T> function) {
        super();
        this.lambda = function;
    }

    public T apply(List<T> sampleRowValues) {
                // It will call the same function on all its children. Eventually the children
                // will be constants or a feature, then it will bubble those values back up the
                // line to pass on to function.
                List<T> values = new LinkedList<>();

                for (Node<T> n : getChildren()) {
                    values.add(n.apply(sampleRowValues));
                }

                return getLambda().apply(values);
    }

    @Override
    public String toString() {
        StringBuilder output = new StringBuilder();

        if (getChildren().size() > 0) {
            output.append(" ( ").append(getLambda());
            getChildren().forEach(output::append);
            output.append(" ) ");
        }
        else {
            output.append(getLambda().toString());
        }

        return output.toString();
    }

    public int getNumberOfChildren() {
        // By returning the arity any excess child nodes will be skipped during evaluation
        return getLambda().getArity();
    }

    private FunctionalPrimitive<T, T> getLambda() {
        return lambda;
    }

    @Override
    public Node<T> clone() {
        Node<T> clone = new FunctionNode<>(getLambda());

        if (getChildren().size() < 1) {
            return clone;
        }

        clone.addChildren(getChildren().stream().map(Node::clone).collect(Collectors.toList()));

        return clone;
    }
}
