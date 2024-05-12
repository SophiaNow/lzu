package org.hbrs.lzu.state;

import org.hbrs.lzu.Component;

import java.lang.reflect.InvocationTargetException;

public abstract class State {

    protected Component component;

    public State(Component component) {
        this.component = component;
    }

    public abstract String getStateName();
 
    public abstract void init() throws InvocationTargetException, IllegalAccessException;

    public abstract void stopComponent() throws InvocationTargetException, IllegalAccessException;

    public abstract boolean deleteComponent();

}
