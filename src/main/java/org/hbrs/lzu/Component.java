package org.hbrs.lzu;

import annotations.Start;
import annotations.Stop;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

public class Component {
    // Todo: braucht man wirklich AtomicBoolean oder reicht einfach state Abfrage?
    private final AtomicBoolean running = new AtomicBoolean(false);


    // State-Pattern anwenden
    public enum State {
        DEPLOYED, // loaded
        RUNNING,
        STOPPED,
        FAULTY
    }

    private State state;
    // private final String name;

    private final URL url;

    private final UUID id;
    private final Class<?> startingClass;

    private Thread thread;


    public Component(UUID id, URL url, Class<?> startingClass) {
        this.id = id;
        // this.name = name;
        this.url = url;
        this.state = State.DEPLOYED;
        this.startingClass = startingClass;
        this.thread = null;
    }

    public void stopComponent() throws InvocationTargetException, IllegalAccessException, InterruptedException {
        // this.state = State.STOPPED;
        // this.running.set(false);
        Method[] methods = startingClass.getDeclaredMethods();
        for (Method m : methods) {
            if (m.getAnnotation(Stop.class) != null) {
                m.invoke(null);
            }
        }
        if (thread != null && thread.isAlive()) {
            thread.interrupt();
            // Todo: so wichtig?
            thread.join();
        }
    }

    public void startComponent(){
        if (thread == null || !thread.isAlive()) {
            thread = new Thread(() -> {
                Method[] methods = startingClass.getDeclaredMethods();
                for (Method m : methods) {
                    if (m.getAnnotation(Start.class) != null) {
                        try {
                            m.invoke(null);
                        } catch (IllegalAccessException | InvocationTargetException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
                System.out.println("IsRunning: "+ this.isRunning());
            });
            thread.start();
        }
    }


    public boolean isRunning() {
        return this.running.get();
    }

}
