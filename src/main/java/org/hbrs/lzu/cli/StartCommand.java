package org.hbrs.lzu.cli;

import org.hbrs.lzu.RuntimeEnvironment;
import org.hbrs.lzu.cli.Command;

import java.util.UUID;

public class StartCommand implements Command {
    private final RuntimeEnvironment environment;
    private final UUID componentId;

    public StartCommand(RuntimeEnvironment environment, UUID componentId) {
        this.environment = environment;
        this.componentId = componentId;
    }

    @Override
    public void execute() throws Exception {
        environment.startComponent(componentId);
    }
}
