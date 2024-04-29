package org.hbrs.assemblerClient;

public class Main {

    private static final Object lock = new Object();
    public static void main(String[] args) throws InterruptedException {
        for (int i = 0; i < 10; ++i) {
            lock.wait(3000);
        }
    }
}
