package org.hbrs.lzu;

import java.net.URL;

public class Component {

    public enum State {
        DEPLOYED, // loaded
        RUNNING,
        STOPPED,
        FAULTY
    }

    private State state;
    private final String name;

    private final URL url;

    private Class<?> startingClass;

    public Component(String name, URL url, Class<?> startingClass) {
        this.name = name;
        this.url = url;
        this.state = State.DEPLOYED;
        this.startingClass = startingClass;
    }

    public Component(String name, URL url) {
        this(name, url, null);
    }
    public String getName() {
        return this.name;
    }

    public void stop() {
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
