package org.hbrs.lzu;

import annotations.Inject;
import annotations.Start;
import org.hbrs.lzu.logging.LoggerImpl;
import org.hbrs.lzu.state.Disposed;
import org.hbrs.lzu.state.Running;
import org.hbrs.lzu.state.State;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.UUID;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class RuntimeEnvironment {

    private static RuntimeEnvironment INSTANCE = null;
    private final HashMap<UUID, Component> components = new HashMap<>();
    private final HashMap<UUID, Thread> threads = new HashMap<>();

    public static RuntimeEnvironment getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new RuntimeEnvironment();
        }
        return INSTANCE;
    }

    private RuntimeEnvironment() {
    }

    public void stop() {
        for (Component comp : components.values()) {
            if (comp.getState() instanceof Running) {
                comp.stopComponent();
                Thread thread = threads.get(comp.getId());
                if (thread.isAlive()) {
                    thread.interrupt(); // wirklich interrupt?! (onhold?
                }
            }
        }
    }

    public UUID deployComponent(String jarPath, String name) throws Exception {
        JarFile jarFile = new JarFile(jarPath);
        Enumeration<JarEntry> entry = jarFile.entries();
        URL[] urls = {new URL("jar:file:" + jarPath + "!/")};
        // URL cl für JARS oder dirs mit .class-Dateien
        URLClassLoader loader = URLClassLoader.newInstance(urls);
        UUID componentId = null;
        try {
            Class<?> startingClass = null;
            while (entry.hasMoreElements()) {
                JarEntry e = entry.nextElement();
                if (e.isDirectory() || !e.getName().endsWith(".class")) {
                    continue;
                }
                String className = e.getName().substring(0, e.getName().length() - 6);
                className = className.replace('/', '.');
                Class clazz = loader.loadClass(className);
                Method[] methods = clazz.getDeclaredMethods();
                boolean injectLogger = doInjectLogger(clazz);

                // Find starting class and store in component
                for (Method m : methods) {
                    if (m.isAnnotationPresent(Start.class)) {
                        startingClass = clazz;
                        System.out.println("Classloader: " + clazz.getClassLoader());
                    }

                    if(injectLogger){
                        injectLogger(m);
                    }
                }

                // Thread mit Component-Instanz assoziieren
                componentId = UUID.randomUUID();
                Component component = new Component(componentId, urls[0], startingClass);
                component.setName(name);
                Thread thread = new Thread(component);
                threads.put(componentId, thread);
                components.put(componentId, component);
                System.out.println("New component added!");
            }
        } catch (ClassNotFoundException e) {
            throw new Exception(e);
        } finally {
            jarFile.close();
            loader.close();
        }
        return componentId;
    }

    private static void injectLogger(Method m) {
        if("setLogger".equals(m.getName())){
            try {
                m.invoke(null, new LoggerImpl());
            } catch (IllegalAccessException e) {
                System.err.println("Method setLogger is not public.");
            } catch (InvocationTargetException e) {
                System.err.println("Method setLogger is not invokable, because of missing/wrong logger parameter.");
            }
        }
    }

    private static boolean doInjectLogger(Class clazz) {
        Field[] fields = clazz.getDeclaredFields();
        for (Field f: fields){
            Inject annotation = f.getAnnotation(Inject.class);
            if (annotation != null) {
                return true;
            }
        }
        return false;
    }

    public void startComponent(UUID id) throws InvocationTargetException, IllegalAccessException {
        Thread thread = threads.get(id);
        if (thread != null && thread.isAlive()) {
            this.components.get(id).init();
        } else {
            thread.start(); //TODO Error handling, if component assembler did not deploy component / gave wrong id
        }
        System.out.println("Component:\n" + components.get(id).toString());
    }


    public boolean stopComponent(UUID id) {
        Thread thread = threads.get(id);
        boolean stopped = false;
        if (thread != null && !thread.isInterrupted()) {
            components.get(id).stopComponent();
            thread.interrupt();
            threads.put(id, new Thread(components.get(id))); // Todo: New component for stopped component
        }
        return stopped;
    }

    public boolean deleteComponent(UUID id) {
        Thread thread = threads.get(id);
        boolean disposable = false;
        if (thread != null) { // vorher: && isAlive(), jetzt nach Stop neuer Thread => nicht Alive
                disposable = this.components.get(id).deleteComponent();
                if (disposable) { // wenn Component Running, nicht löschbar
                thread.interrupt(); // Nur zur Sicherheit
                this.threads.remove(id);
                this.components.remove(id);
            }
        }
        return disposable;
    }
    public HashMap<UUID, Component> getComponents() {
        return this.components;
    }

    public HashMap<UUID, Thread> getThreads() {
        return this.threads;
    }

    public State getComponentState(UUID uuid) {
        Component comp = this.components.get(uuid);
        if (comp == null) {
            return new Disposed(null);
        }
        return comp.getState();
    }
}