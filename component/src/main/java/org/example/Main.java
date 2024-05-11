package org.example;

import annotations.Inject;
import annotations.Logger;
import annotations.Start;
import annotations.Stop;

public class Main {
    @Inject
    private static Logger logger;

    @Start
    public static void start(){
        logger.sendLog("Hello component assembler!");
    }


    @Stop
    public static void stop(){
       logger.sendLog("All systems are shut down!");
    }

    public static void setLogger(Logger logger){
        Main.logger = logger;
    }
}