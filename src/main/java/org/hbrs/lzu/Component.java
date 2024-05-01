package org.hbrs.lzu;

import annotations.Start;
import annotations.Stop;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

public class Component implements Runnable {
    // Todo: braucht man wirklich AtomicBoolean oder reicht einfach state Abfage?
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

    @Override
    public void run() {
        this.running.set(true);
        try {
            this.startComponent();
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public Component(UUID id, URL url, Class<?> startingClass) {
        this.id = id;
        // this.name = name;
        this.url = url;
        this.state = State.DEPLOYED;
        this.startingClass = startingClass;
    }

    public void stopComponent() throws InvocationTargetException, IllegalAccessException {
        this.state = State.STOPPED;
        this.running.set(false);
        Method[] methods = startingClass.getDeclaredMethods();
        for (Method m : methods) {
            if (m.getAnnotation(Stop.class) != null) {
                m.invoke(null);
            }
        }
    }

    private void startComponent() throws InvocationTargetException, IllegalAccessException {
        Method[] methods = startingClass.getDeclaredMethods();
        for (Method m : methods) {
            if (m.getAnnotation(Start.class) != null) {
                m.invoke(null);
            }
        }
        System.out.println("IsRunning: "+ this.isRunning());
    }

    public Class<?> getStartingClass() {
        return this.startingClass;
    }

    public boolean isRunning() {
        return this.running.get();
    }

}
