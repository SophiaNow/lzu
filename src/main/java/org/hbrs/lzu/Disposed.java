package org.hbrs.lzu;

import java.lang.reflect.InvocationTargetException;

public class Disposed extends State{

    public Disposed(Component component) {
        super(component);
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
    public void deleteComponent() {
     //
    }
}
