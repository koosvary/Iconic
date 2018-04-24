package org.iconic.metaheuristic;

import java.util.*;

import org.iconic.io.Debug;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Trainer {
    private static final Logger logger = LogManager.getLogger(Trainer.class.getName());
    private int numCores;
    private int numThreads;
    private int numGenes;
    private int generation;
    private ArrayList<GeneExpression> bestGenes;
    private List<GenePool> demes;
    private List<Thread> threads;

    public Trainer() {
        this.numThreads = 2;
        this.numGenes = 10;
        this.generation = 0;
        this.demes = new ArrayList<>(numThreads);
        this.threads = new ArrayList<>(numThreads);
        generateThreads();
    }

    public void startSearch() {
        Debug.out("Trainer  startSearch()");

        for (Thread thread : threads) {
            thread.start();
        }
    }

    public void stopSearch() {
        for (GenePool deme : demes) {
            deme.setRunning(false);
        }
    }

    public void generateThreads() {
        logger.debug("Trainer::generateThreads() called with {} threads", () -> numThreads);

        for (int i = 0; i < numThreads; i++) {
            GenePool deme = new GenePool(i, numGenes);
            demes.add(deme);
            threads.add(new Thread(demes.get(i)));
        }
    }
}