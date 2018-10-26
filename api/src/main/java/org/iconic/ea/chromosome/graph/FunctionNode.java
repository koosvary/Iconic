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
            output.append(getLambda()).append(" ( ");
            List<Node<T>> children = getChildren();
            for(int i = 0; i < children.size(); i++){
                if(i < children.size()-1){
                    output.append(children.get(i).toString() + ", ");
                }
                else{
                    output.append(children.get(i).toString());
                }
            }
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
