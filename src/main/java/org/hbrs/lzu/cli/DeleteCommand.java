package org.hbrs.lzu.cli;

import org.hbrs.lzu.RuntimeEnvironment;
import org.hbrs.lzu.cli.Command;

import java.util.UUID;

public class DeleteCommand implements Command {
    private final UUID id;
    private final RuntimeEnvironment rte;

    public DeleteCommand(RuntimeEnvironment rte, UUID id) {
        this.rte = rte;
        this.id = id;
    }

    @Override
    public void execute() throws Exception {
        if (!this.rte.deleteComponent(id)) {
            System.out.println("Component " + this.id + " is still running!");
        } else {
            System.out.println("Component " + this.id + " deleted!");

        }
    }
}
