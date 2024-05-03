package org.hbrs.lzu;

import annotations.Stop;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Running extends State {

    public Running(Component component) {
        super(component);
    }

    @Override
    public void init() {
    }

    @Override
    public void stopComponent() throws InvocationTargetException, IllegalAccessException {
        Method[] methods = this.component.startingClass.getDeclaredMethods();
        for (Method m : methods) {
            if (m.getAnnotation(Stop.class) != null) {
                m.invoke(null);
            }
        }
        this.component.setState(new Stopped(this.component));
    }

    @Override
    public void deleteComponent() {

    }
}
