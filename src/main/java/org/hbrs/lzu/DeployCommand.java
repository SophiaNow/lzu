package org.hbrs.lzu;

public class DeployCommand implements Command {
    private final RuntimeEnvironment environment;
    private final String jarPath;

    public DeployCommand(RuntimeEnvironment environment, String jarPath) {
        this.environment = environment;
        this.jarPath = jarPath;
    }

    @Override
    public void execute() throws Exception {
        System.out.println("Component id: " + environment.deployComponent(jarPath));
    }
}
