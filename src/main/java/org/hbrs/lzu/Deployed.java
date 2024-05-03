package org.hbrs.lzu;

import annotations.Start;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Deployed extends State {

    @Override
    public void init() throws InvocationTargetException, IllegalAccessException {
        Method[] methods = this.component.startingClass.getDeclaredMethods();
        for (Method m : methods) {
            if (m.getAnnotation(Start.class) != null) {
                m.invoke(null);
            }
        }
        this.component.setState(new Running(this.component));
    }

    public Deployed(Component component) {
        super(component);
    }

    @Override
    public void stopComponent() {
        // Do nothing;
    }

    @Override
    public void deleteComponent() {
        // Do nothing!
    }
}
