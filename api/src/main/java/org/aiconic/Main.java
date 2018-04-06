package org.aiconic;

import org.aiconic.io.Config;
import org.aiconic.io.DataManager;
import org.aiconic.io.Debug;
import org.aiconic.metaheuristic.Trainer;

public class Main
{
    public static void main(String[] args)
    {
        Debug.out("Main args[]");
        for (String arg : args)
            Debug.out(arg);
        
        Config config = new Config();
        DataManager DM = new DataManager();
        DM.importData(args[0]);
        DM.normalizeScale();
        Trainer trainer = new Trainer();
        
        trainer.startSearch();
        
        /*
        */
    }
}