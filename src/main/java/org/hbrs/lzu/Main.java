package org.hbrs.lzu;

import java.lang.reflect.InvocationTargetException;
import java.util.UUID;

public class Main {

    public static void main(String[] args) throws InvocationTargetException, IllegalAccessException {
        String jarPath = "C:\\Users\\sophi\\Informatik\\2\\OOKA\\Uebungen\\Uebung2\\lzu\\component\\target\\component-1.0-SNAPSHOT.jar";
        RuntimeEnvironment rte = RuntimeEnvironment.getInstance();
        UUID id1 = null;
        UUID id2 = null;
        try {
            id1 = rte.deployComponent(jarPath, "comp1");
            id2 = rte.deployComponent(jarPath, "comp2");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        rte.startComponent(id1);
        rte.startComponent(id2);
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            return;
        }
        rte.stopComponent(id2);
        try {
            Thread.sleep(5000);
        } catch (InterruptedException ex) {
            return;
        }
        rte.stopComponent(id1);
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            return;
        }
        rte.startComponent(id1);
        rte.stopComponent(id1);

        rte.deleteComponent(id1);
        rte.deleteComponent(id2);
        // rte.stop();
    }
}