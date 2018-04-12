package org.aiconic;

import org.aiconic.io.Config;
import org.aiconic.io.DataManager;
import org.aiconic.metaheuristic.Trainer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;

public class Main {
    private static final Logger logger = LogManager.getLogger(DataManager.class);

    public static void main(String[] args) {
        logger.traceEntry();
        logger.debug("Args: {}", Arrays.toString(args));

//        Config config = new Config();
        DataManager DM = new DataManager();
        DM.importData(args[0]);
        DM.normalizeScale();
        Trainer trainer = new Trainer();

        trainer.startSearch();

        logger.traceExit();
    }
}