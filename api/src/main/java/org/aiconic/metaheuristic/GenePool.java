package org.aiconic.metaheuristic;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.Calendar;
import java.text.SimpleDateFormat;
import java.text.NumberFormat;
import java.text.DecimalFormat;

public class GenePool implements Runnable {
    private static final Logger logger = LogManager.getLogger(GenePool.class);
    private boolean running;
    private int id;
    private int numGenes;
    private GeneExpression[] genes;
    private GeneExpression bestGene;


    GenePool(int id, int numGenes) {
        this.id = id;
        this.numGenes = numGenes;
        this.running = false;
        generateGenes();
    }

    public void generateGenes() {
        genes = new GeneExpression[numGenes];

        for (int i = 0; i < numGenes; i++) {
            genes[i] = new GeneExpression();
        }
    }

    public void run() {
        logger.traceEntry();
        List<Double> scores;
        int bestScoreIndex;
        boolean newBest;

        setRunning(true);

        do {
            newBest = false;
            scores = new ArrayList<Double>();
            bestScoreIndex = 0;

            for (int i = 0; i < genes.length; i++) {
                final int index = i;
                final GeneExpression gene = genes[i];
                double fit = gene.calculate();
                scores.add(fit);

                if (fit < scores.get(bestScoreIndex) || Double.isNaN(scores.get(bestScoreIndex))) {
                    logger.debug("GenePool {}'s score[{}] = {}", () -> id, () -> index, () -> fit);
                    bestScoreIndex = i;
                    Calendar cal = Calendar.getInstance();
                    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss.SS");
                    NumberFormat formatter = new DecimalFormat("#0.000000");
                    logger.info(
                            "GenePool {}: {}\n\tFit: {}\n\tSize: {}\n\tSolution: {}",
                            () -> id,
                            () -> sdf.format(cal.getTime()),
                            () -> formatter.format(fit),
                            gene::getSize,
                            gene::getExpressionString
                    );
                }
            }

            genes[0] = genes[bestScoreIndex];

            for (int i = 1; i < scores.size(); i++) {
                genes[i] = new GeneExpression();
            }
        }
        while (scores.get(bestScoreIndex) > 0.3 && isRunning());

        String[] expression = genes[0].getExpression();
        StringBuilder output = new StringBuilder();

        for (String s : expression)
            output.append(s).append(", ");

        final double bestScore = scores.get(bestScoreIndex);

        logger.info(
                "GenePool {}\tBest expression = {}\t Best score = {}",
                () -> id,
                () -> output,
                () -> bestScore
        );
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }
}