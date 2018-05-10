package org.iconic;

import lombok.extern.log4j.Log4j2;
import org.iconic.io.DataManager;
import org.iconic.metaheuristic.Trainer;

import java.util.Arrays;

@Log4j2
public class Main {
    public static void main(String[] args) {
        log.traceEntry();
        log.debug("Args: {}", Arrays.toString(args));

//        Config config = new Config();
        DataManager DM = new DataManager();
        DM.importData(args[0]);
        DM.normalizeScale();
        Trainer trainer = new Trainer();

        trainer.startSearch();

        log.traceExit();
    }
}