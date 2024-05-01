package org.hbrs.lzu;

import java.lang.reflect.InvocationTargetException;
import java.util.UUID;

public class Main {

    public static void main(String[] args) {
        RuntimeEnvironment rte = RuntimeEnvironment.getInstance();
        UUID id = null;
        try {
            id = rte.deployComponent("C:\\Users\\sophi\\Informatik\\2\\OOKA\\Uebungen\\Uebung2\\lzu\\component\\target\\component-1.0-SNAPSHOT.jar");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        rte.startComponent(id);
        try {
            rte.stopComponent(id);
        } catch (InterruptedException | InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        rte.startComponent(id);
        rte.stop();
    }
}
