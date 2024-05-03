package org.hbrs.lzu;

import java.lang.reflect.InvocationTargetException;
import java.util.UUID;

public class Main {

    public static void main(String[] args) throws InvocationTargetException, IllegalAccessException {
        RuntimeEnvironment rte = RuntimeEnvironment.getInstance();
        UUID id = null;
        try {
            id = rte.deployComponent("C:\\Users\\sophi\\Informatik\\2\\OOKA\\Uebungen\\Uebung2\\lzu\\component\\target\\component-1.0-SNAPSHOT.jar");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        rte.startComponent(id);
        try {
            Thread.sleep(5000);
        } catch (InterruptedException ex) {
            return;
        }
        rte.stopComponent(id);
        rte.startComponent(id);
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            return;
        }
        rte.stopComponent(id);
        rte.deleteComponent(id);
        rte.stop();
    }
}