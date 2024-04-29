package org.hbrs.lzu;

public class Main {
    public static void main(String[] args) {
        RuntimeEnvironment rte = new RuntimeEnvironment();
        rte.start();
        // rte.run();
        try {
            rte.deployComponents("C:\\Users\\sophi\\Informatik\\2\\OOKA\\Uebungen\\Uebung2\\lzu\\lzu\\assemblerClient\\target\\assemblerClient-1.0-SNAPSHOT.jar");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        rte.stop();
    }
}
