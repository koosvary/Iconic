package org.aiconic.metaheuristic;

import java.util.*;
import org.aiconic.io.Debug;

public class Trainer
{
    private int numCores = Runtime.getRuntime().availableProcessors();
    private int numThreads = 2;
    private int numGenes = 10;
    private int generation = 0;
    private ArrayList<GeneExpression> bestGenes;
    private GenePool[] genePool;
    private Thread[] threads;
    
    public Trainer()
    {
        generateThreads();
    }
    
    public void startSearch()
    {
        Debug.out("Trainer  startSearch()");
        for (int i = 0; i < threads.length; i++)
        {
            threads[i].start();
        }
    }
    
    public void generateThreads()
    {
        Debug.out("Trainer  generateThreads()");
        Debug.out("Trainer  Number of threads: " + numThreads);
        threads = new Thread[numThreads];
        
        for (int i = 0; i < threads.length; i++)
        {
            threads[i] = new Thread(new GenePool(i, numGenes));
        }
    }
}