package org.hbrs.lzu.state;

import annotations.Start;
import org.hbrs.lzu.Component;
import org.hbrs.lzu.state.Disposed;
import org.hbrs.lzu.state.Running;
import org.hbrs.lzu.state.State;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Stopped extends State {

    public Stopped(Component component) {
        super(component);
    }

    @Override
    public void init() throws InvocationTargetException, IllegalAccessException {
        // Nothing;
        Method[] methods = this.component.startingClass.getDeclaredMethods();
        for (Method m : methods) {
            if (m.getAnnotation(Start.class) != null) {
                m.invoke(null);
            }
        }
        this.component.setState(new Running(this.component));
    }

    @Override
    public void stopComponent() {
        // Do nothing!
    }

    @Override
    public boolean deleteComponent() {
        this.component.setState(new Disposed(this.component));
        return true;
    }
}
