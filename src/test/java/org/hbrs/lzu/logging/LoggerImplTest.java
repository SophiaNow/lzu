package org.hbrs.lzu.logging;

import annotations.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LoggerImplTest {

    private static final String LOG_TEMPLATE = "++++ LOG: Meldung aus Component:";
    Logger logger;

    @BeforeEach
    void setUp() {
        logger = new LoggerImpl();
    }

    @Test
    void loggingTemplateTest(){
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        System.setOut(new java.io.PrintStream(out));
        logger.sendLog("deploy component");

        assertTrue(out.toString().contains(LOG_TEMPLATE));
    }
}
