package org.hbrs.lzu;

import annotations.Start;
import annotations.Stop;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class RuntimeEnvironment {

    private static RuntimeEnvironment INSTANCE = null;
    // private final ConcurrentHashMap<UUID, Thread> threads = new ConcurrentHashMap<>(); // Todo: concurrent Map of Threads und dann getCurrentThread() der Component
    private final ConcurrentHashMap<UUID, Component> components = new ConcurrentHashMap<>();

    public static RuntimeEnvironment getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new RuntimeEnvironment();
        }
        return INSTANCE;
    }

    private RuntimeEnvironment() {
    }

    public void stop() {
        try {
            for (Component comp : components.values()) {
                if (comp.isRunning()) {
                    comp.stopComponent();
                }
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    // Todo: for every component own thread
    public UUID deployComponent(String jarPath) throws Exception {
        // if (!running.get()) {
        //     System.out.println("Cannot deploy components, runtime is not running.");
        //     return;
        // }
        // Perform deployment logic here
        JarFile jarFile = new JarFile(jarPath); // Runtime.Version to assure compatibility of loaded classes with current runtime?
        Enumeration<JarEntry> entry = jarFile.entries();
        URL[] urls = {new URL("jar:file:" + jarPath + "!/")};
        // URL cl für JARS oder dirs mit .class-Dateien
        URLClassLoader loader = URLClassLoader.newInstance(urls);
        UUID componentId = null;
        try {
            // Todo: Read out component/module name from module-info.java?
            String name = "Component";
            Class<?> startingClass = null;
            while (entry.hasMoreElements()) {
                JarEntry e = entry.nextElement();
                if (e.isDirectory() || !e.getName().endsWith(".class")) {
                    continue;
                }
                String className = e.getName().substring(0, e.getName().length() - 6);
                className = className.replace('/', '.');
                System.out.println("className: " + className);
                Class<?> clazz = loader.loadClass(className);
                Method[] methods = clazz.getDeclaredMethods();
                // Find starting class and store in component
                for (Method m: methods) {
                    if (m.isAnnotationPresent(Start.class)) {
                        startingClass = clazz;
                    }

                }
                // Thread mit Component-Instanz assoziieren
                componentId = UUID.randomUUID();
                Component component = new Component(componentId, urls[0], startingClass);
                // Thread thread = new Thread(component);
                // threads.put(componentId, thread);
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

    public void startComponent(UUID id) {
        // Thread thread = threads.get(id);
        // Todo: Dopplung des States (Running / Alive) in Component und Thread
        // if (thread != null && !thread.isAlive()) {
        //     thread.start();
        // }
        components.get(id).startComponent();
    }

    public void stopComponent(UUID id) throws InterruptedException, InvocationTargetException, IllegalAccessException {
        components.get(id).stopComponent();
        // // Thread thread = threads.get(id);
        // if (thread != null && thread.isAlive()) {
        //     try {
        //         components.get(id).stopComponent();
        //     } catch (InvocationTargetException | IllegalAccessException e) {
        //         throw new RuntimeException(e);
        //     }
        //     thread.interrupt();
        // }
    }

    public void deleteComponent(UUID id) {
        // Todo: deploy, start, stop, delete Component
    }

}
