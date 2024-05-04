package org.hbrs.lzu.cli;

import org.hbrs.lzu.RuntimeEnvironment;
import org.hbrs.lzu.cli.Command;

import java.util.UUID;

public class StopCommand implements Command {
    private final RuntimeEnvironment environment;
    private final UUID componentId;
    public StopCommand(RuntimeEnvironment rte, UUID id) {
        this.componentId = id;
        this.environment = rte;
    }

    @Override
    public void execute() throws Exception {
        this.environment.stopComponent(this.componentId);
    }
}
