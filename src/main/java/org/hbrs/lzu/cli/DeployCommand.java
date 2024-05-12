package org.hbrs.lzu.cli;

import org.hbrs.lzu.RuntimeEnvironment;

import java.util.UUID;

public class DeployCommand implements Command {
    private final RuntimeEnvironment environment;
    private final String jarPath;
    private final UUID id;
    private final String name;

    public DeployCommand(RuntimeEnvironment environment, String jarPath, UUID id, String name) {
        this.environment = environment;
        this.jarPath = jarPath;
        this.id = id;
        this.name = name.toLowerCase();
    }

    public DeployCommand(RuntimeEnvironment environment, String jarPath, String name) {
        this.environment = environment;
        this.jarPath = jarPath;
        this.id = null;
        this.name = name.toLowerCase();
    }

    @Override
    public void execute() throws Exception {
        System.out.println("Component id: " + environment.deployComponent(jarPath, this.name, this.id));
    }
}
