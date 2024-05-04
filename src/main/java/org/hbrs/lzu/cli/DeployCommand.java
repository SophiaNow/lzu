package org.hbrs.lzu.cli;

import org.hbrs.lzu.RuntimeEnvironment;
import org.hbrs.lzu.cli.Command;

public class DeployCommand implements Command {
    private final RuntimeEnvironment environment;
    private final String jarPath;
    private final String name;

    public DeployCommand(RuntimeEnvironment environment, String jarPath, String name) {
        this.environment = environment;
        this.jarPath = jarPath;
        this.name = name.toLowerCase();
    }

    @Override
    public void execute() throws Exception {
        System.out.println("Component id: " + environment.deployComponent(jarPath, this.name));
    }
}
