package org.hbrs.lzu;

import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

public class Component implements Runnable {
    private final AtomicBoolean running = new AtomicBoolean(false);
    private State state;
    private final URL url;
    private final UUID id;
    protected final Class<?> startingClass;
    private String name;

    public Component(UUID id, URL url, Class<?> startingClass) {
        this.id = id;
        // this.name = name;
        this.url = url;
        this.startingClass = startingClass;
        this.state = new Deployed(this);
    }

    @Override
    public void run() {
        try {
            this.init();
            while (this.state instanceof Running || this.state instanceof Stopped) {
                if (Thread.currentThread().isInterrupted()) {
                    return;
                }

            }
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public void init() throws InvocationTargetException, IllegalAccessException {
        this.state.init();
    }

    public void stopComponent() {
        try {
            this.state.stopComponent();
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteComponent() {
        this.state.deleteComponent();
    }


    public boolean isRunning() {
        return this.running.get();
    }

    public UUID getId() {
        return this.id;
    }

    public void setState(State state) {
        this.state = state;
    }

    public State getState() {
        return this.state;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        String string = "[";
        string += this.name + ", ";
        string += this.id + ", ";
        String state = this.state.getClass().getName();
        string += state.substring(state.lastIndexOf('.') + 1) + "]";
        return string;
    }
}
