package org.hbrs.lzu;

import java.net.URL;

// Todo: for each comopnent different thread: istances
public class Component extends Thread {

    public enum State {
        DEPLOYED, // loaded
        RUNNING,
        STOPPED,
        FAULTY
    }

    private State state;
    private final String name;

    private final Thread thread;

    private final URL url;

    private Class<?> startingClass;

    public Component(String name, URL url, Class<?> startingClass, Thread thread) {
        this.thread = thread;
        this.name = name;
        this.url = url;
        this.state = State.DEPLOYED;
        this.startingClass = startingClass;
    }

    public String getComponentName() {
        return this.name;
    }

    public void stopComponent() {
        //Todo: stop component?!
        this.state = State.STOPPED;
    }

    public boolean execute() {
        if (this.state != State.DEPLOYED) {
            return false;
        }
        System.out.println("Component " + this.name + " is executed!");
        this.state = State.RUNNING;
        return true;
    }

    public Class<?> getStartingClass() {
        return this.startingClass;
    }

}
