package org.hbrs.lzu;

import java.lang.reflect.InvocationTargetException;

public abstract class State {

    protected Component component;

    public State(Component component) {
        this.component = component;
    }

    public abstract void init() throws InvocationTargetException, IllegalAccessException;

    public abstract void stopComponent() throws InvocationTargetException, IllegalAccessException;

    public abstract void deleteComponent();

}
