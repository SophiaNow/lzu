package org.hbrs.lzu;

public class Component {

    public enum State {
        DEPLOYED, // loaded
        RUNNING,
        STOPPED,
        FAULTY
    }

    private State state;
    private final String name;

    private final String url;

    private final String startingClass;

    public Component(String name, String url, String startingClass) {
        this.name = name;
        this.url = url;
        // this.state = State.INITIALISED;
        this.startingClass =startingClass;
    }
    public String getName() {
        return this.name;
    }

    public void deploy() {
        Deployer deployer = new Deployer();
        try {
            deployer.deployComponent(url);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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

}
