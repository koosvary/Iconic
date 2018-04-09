package org.aiconic.metaheuristic;

import java.util.*;
import java.util.Calendar;
import java.text.SimpleDateFormat;
import java.text.NumberFormat;
import java.text.DecimalFormat;

public class GenePool implements Runnable
{
    private boolean running;
    private int id;
    private int numGenes;
    private GeneExpression[] genes;
    private GeneExpression bestGene;
    
    
    GenePool(int id, int numGenes)
    {
        this.id = id;
        this.numGenes = numGenes;
        this.running = false;
        generateGenes();
    }
    
    public void generateGenes()
    {
        genes = new GeneExpression[numGenes];
        
        for (int i = 0; i < numGenes; i++)
        {
            genes[i] = new GeneExpression();
        }
    }
    
    public void run()
    {
        List<Double> scores;
        int bestScoreIndex;
        boolean newBest;

        setRunning(true);

        do
        {
            //Debug.out("GenePool " + id + " run()");
            newBest = false;
            scores = new ArrayList<Double>();
            bestScoreIndex = 0;
            
            for (int i = 0; i < genes.length; i++)
            {
                double fit = genes[i].calculate();
                scores.add(fit);
                
                if (fit < scores.get(bestScoreIndex) || Double.isNaN(scores.get(bestScoreIndex))) 
                {
                    bestScoreIndex = i;
                    //Debug.out("GenePool " + id + " score[" + i + "] = " + fit);
                    Calendar cal = Calendar.getInstance();
                    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss.SS");
                    NumberFormat formatter = new DecimalFormat("#0.000000");  
                    System.out.println( sdf.format(cal.getTime()) + " > New solution: Fit: " + formatter.format(fit) + " Size: " + genes[i].getSize() + " Solution: " + genes[i].getExpressionString());
                }
            }
            
            genes[0] = genes[bestScoreIndex];
            
            for (int i = 1; i < scores.size(); i++) 
            {
                genes[i] = new GeneExpression();
            }
        }
        while (scores.get(bestScoreIndex) > 0.3 && isRunning());
            
        String[] expression = genes[0].getExpression();
        
        String output = "";
        
        for (String s : expression)
            output += s + ", ";
        
        System.out.println("GenePool " + id + " Best expression = " + output + " Best score = " + scores.get(bestScoreIndex));
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }
}