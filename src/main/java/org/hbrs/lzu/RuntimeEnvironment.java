package org.hbrs.lzu;

import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class RuntimeEnvironment implements Runnable {

    private ConcurrentHashMap<UUID, Component> components = new ConcurrentHashMap<>(); // Todo: concurrent Map of Threads und dann getCurrentThread() der Component
    private final AtomicBoolean running = new AtomicBoolean(false);

    // public void start() {
    //
    // }

    public synchronized void stop() {
        this.running.set(false);
    }

    @Override
    public void run() {
        this.running.set(true);
        while (this.running.get()) {
            try {
                Thread.sleep(1000);
                System.out.println("Waiting for input...");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("Runtime was stopped, failed to complete operation!");
            }
        }
    }

    // Todo: for every component own thread
    public synchronized void deployComponents(String jarPath) throws Exception {
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
                Class<?> c = loader.loadClass(className);
                // Todo: Find starting class and store in component
                // if (c.isAnnotationPresent(Startable.class)) {
                // }
                if (className.endsWith("Main")) {
                    startingClass = c;
                    System.out.println("Invoke starting method!");
                    Method main = c.getMethod("main", String[].class);
                    String[] params = null;
                    main.invoke(null, (Object) params);
                }
            }
            // Todo: current Thread an Componente übergeben? Oder Component öffnet eigenen Thread
            Component component = new Component(name, urls[0], startingClass, Thread.currentThread());
            components.put(UUID.randomUUID(), component);
            System.out.println("New component added!");
        } catch (ClassNotFoundException e) {
            throw new Exception(e);
        } finally {
            jarFile.close();
            loader.close();
        }
    }

    public synchronized void deleteComponent() {
        // Todo: deploy, start, stop, delete Component
    }

}
