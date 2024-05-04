package org.hbrs.lzu;

import java.util.UUID;

public class GetStateCommand implements Command {

    private final RuntimeEnvironment rte;
    private final UUID uuid;

    public GetStateCommand(RuntimeEnvironment rte, UUID uuid) {
        this.rte = rte;
        this.uuid = uuid;
    }

    @Override
    public void execute() throws Exception {
        String state = this.rte.getComponentState(uuid).getClass().getName();
        System.out.println("Component " + uuid + " has State " + state.substring(state.lastIndexOf('.') + 1));
    }
}
