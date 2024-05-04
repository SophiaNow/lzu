package org.hbrs.lzu;

import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.UUID;

public class Component implements Runnable {
    private State state;
    private final URL url;
    private final UUID id;
    protected final Class<?> startingClass;
    private String name;

    public Component(UUID id, URL url, Class<?> startingClass) {
        this.id = id;
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

    public boolean deleteComponent() {
        return this.state.deleteComponent();
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

    public String getName() {
        return this.name;
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
