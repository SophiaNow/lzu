package org.hbrs.lzu.state;

import org.hbrs.lzu.Component;

import java.lang.reflect.InvocationTargetException;

public class Disposed extends State {

    public Disposed(Component component) {
        super(component);
    }

    @Override
    public String getStateName() {
        return "Disposed";
    }

    @Override
    public void init() throws InvocationTargetException, IllegalAccessException {
        //
    }

    @Override
    public void stopComponent() throws InvocationTargetException, IllegalAccessException {
        //
    }

    @Override
    public boolean deleteComponent() {
        return false;
    }
}
