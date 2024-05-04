package org.hbrs.lzu.state;

import annotations.Stop;
import org.hbrs.lzu.Component;

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
        Method[] methods = this.component.getStartingClass().getDeclaredMethods();
        for (Method m : methods) {
            if (m.getAnnotation(Stop.class) != null) {
                m.invoke(null);
            }
        }
        this.component.setState(new Stopped(this.component));
    }

    @Override
    public boolean deleteComponent() {
        return false;
    }
}
