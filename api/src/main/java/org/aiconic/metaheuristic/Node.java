package org.aiconic.metaheuristic;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

public class Node {
    private static final Logger logger = LogManager.getLogger(Node.class);
    private Function function;
    private Node[] children;

    Node() {
    }

    Node(String[] expression) {
        generateTree(expression);
    }

    public void generateTree(String[] expression) {
        List<String> expressionList = new LinkedList<String>();
        expressionList.addAll(Arrays.asList(expression));
        generateTree(expressionList);
    }

    public List<String> generateTree(List<String> expression) {
        logger.traceEntry();
        logger.debug("expression = {}", expression.toString());

        function = new Function(expression.remove(0));

        int numChildren = function.getChildren();
        children = new Node[numChildren];

        for (int i = 0; i < numChildren; i++) {
            children[i] = new Node();
        }

        for (int i = 0; i < numChildren; i++) {
            expression = children[i].generateTree(expression);
        }

        logger.traceExit();
        return expression;
    }

    public double calculate(int sample) {
        return function.calculate(sample, children);
    }
}