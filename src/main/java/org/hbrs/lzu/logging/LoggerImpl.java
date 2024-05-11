package org.hbrs.lzu.logging;

import annotations.Logger;

import java.sql.Timestamp;
import java.util.Date;

public class LoggerImpl implements Logger {

    @Override
    public void sendLog(String message) {
        System.out.println("++++ LOG: Meldung aus Component: " + message + new Timestamp(System.currentTimeMillis()));
    }
}
