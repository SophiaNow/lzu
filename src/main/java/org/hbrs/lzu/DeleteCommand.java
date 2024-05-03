package org.hbrs.lzu;

import java.util.UUID;

public class DeleteCommand implements Command{
    private final UUID id;
    private final RuntimeEnvironment rte;

    public DeleteCommand(RuntimeEnvironment rte, UUID id) {
        this.rte = rte;
        this.id = id;
    }

    @Override
    public void execute() throws Exception {
        this.rte.deleteComponent(id);
    }
}
