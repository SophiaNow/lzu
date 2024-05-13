package org.hbrs.lzu;

import annotations.Inject;
import annotations.Start;
import org.hbrs.lzu.cli.DeleteCommand;
import org.hbrs.lzu.cli.DeployCommand;
import org.hbrs.lzu.cli.StartCommand;
import org.hbrs.lzu.cli.StopCommand;
import org.hbrs.lzu.logging.LoggerImpl;
import org.hbrs.lzu.state.Disposed;
import org.hbrs.lzu.state.Running;
import org.hbrs.lzu.state.State;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.UUID;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class RuntimeEnvironment {

    private static RuntimeEnvironment INSTANCE = null;
    private final HashMap<UUID, Component> components = new HashMap<>();
    private final HashMap<UUID, Thread> threads = new HashMap<>();
    private boolean modifyCache = false;

    public static RuntimeEnvironment getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new RuntimeEnvironment();
            INSTANCE.getCachedState();
        }
        return INSTANCE;
    }

    private void getCachedState() {
        INSTANCE.modifyCache = false;
        PrintStream originalSystemOut = System.out;
        String fileName = "cache.txt";

        if (!new File(fileName).exists()) {
            System.out.println("No cache file found.");
            return;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            // Disable logs to System.out
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            PrintStream printStream = new PrintStream(byteArrayOutputStream);
            System.setOut(printStream);

            String line;

            // Read lines from the file until there are no more lines
            while ((line = reader.readLine()) != null) {
                // Split the line by comma to get individual fields
                String[] fields = line.split(",");

                // Extract data from the fields array
                UUID id = UUID.fromString(fields[0].trim());
                String name = fields[1].trim();
                String url = fields[2].trim();
                String state = fields[3].trim();

                switch (state) {
                    case "Deployed":
                        try {
                            new DeployCommand(getInstance(), url, id, name).execute();
                        } catch (Exception e) {
                            System.err.println("Error deploying " + name + " to " + url);
                            e.printStackTrace();
                        }
                        break;
                    case "Running":
                        try {
                            new DeployCommand(getInstance(), url, id, name).execute();
                            new StartCommand(getInstance(), id).execute();
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                        break;
                    case "Stopped":
                        try {
                            new DeployCommand(getInstance(), url, id, name).execute();
                            new StartCommand(getInstance(), id).execute();
                            new StopCommand(getInstance(), id).execute(); // TODO issue with sychronization
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                        break;
                    case "Disposed":
                        try {
                            new DeployCommand(getInstance(), url, id, name).execute();
                            new StartCommand(getInstance(), id).execute();
                            new StopCommand(getInstance(), id).execute();
                            new DeleteCommand(getInstance(), id).execute();
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                        break;
                    default:
                        System.err.println("Unknown state: " + state);
                }
            }
        } catch (IOException e) {
            System.out.println("An error occurred while reading the file.");
            e.printStackTrace();
        } finally {
            // Restore the original System.out
            System.setOut(originalSystemOut);

            System.out.println("--- Cached components were restored: ---");
            Iterator<Component> iterator = getInstance().getComponents().values().iterator();
            while (iterator.hasNext()) {
                Component next = iterator.next();
                System.out.println(next.toString());
            }
        }
        INSTANCE.modifyCache = true;
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
        return getInstance().deployComponent(jarPath, name, null);
    }

    public UUID deployComponent(String jarPath, String name, UUID id) throws Exception {
        String PREFIX_URL = "jar:file:";
        String POSTFIX_URL = "!/";
        if (jarPath.startsWith(PREFIX_URL) && jarPath.endsWith(POSTFIX_URL)) {
            jarPath = jarPath.substring(9, jarPath.length() - 2);
        }
        JarFile jarFile = new JarFile(jarPath);
        Enumeration<JarEntry> entry = jarFile.entries();
        URL[] urls = {new URL(PREFIX_URL + jarPath + POSTFIX_URL)};
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
                Class<?> clazz = loader.loadClass(className);
                Method[] methods = clazz.getDeclaredMethods();
                boolean injectLogger = doInjectLogger(clazz);

                // Find starting class and store in component
                for (Method m : methods) {
                    if (m.isAnnotationPresent(Start.class)) {
                        startingClass = clazz;
                        System.out.println("Classloader: " + clazz.getClassLoader());
                    }

                    if (injectLogger) {
                        injectLogger(m);
                    }
                }

                // Thread mit Component-Instanz assoziieren
                componentId = id != null ? id : UUID.randomUUID();
                Component component = new Component(componentId, urls[0], startingClass, name);
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
        if ("setLogger".equals(m.getName())) {
            try {
                m.invoke(null, new LoggerImpl());
            } catch (IllegalAccessException e) {
                System.err.println("Method setLogger is not public.");
            } catch (InvocationTargetException e) {
                System.err.println("Method setLogger is not invokable, because of missing/wrong logger parameter.");
            }
        }
    }

    private static boolean doInjectLogger(Class<?> clazz) {
        Field[] fields = clazz.getDeclaredFields();
        for (Field f : fields) {
            Inject annotation = f.getAnnotation(Inject.class);
            if (annotation != null) {
                return true;
            }
        }
        return false;
    }

    public void startComponent(UUID id) throws InvocationTargetException, IllegalAccessException {
        Thread thread = threads.get(id);
        if (thread == null) {
            System.err.println("Component with id " + id + " not found! Either the id is wrong or the component has not yet been deployed.");
            return;
        }

        if (thread.isAlive()) {
            this.components.get(id).init();
        } else {
            thread.start();
        }

        joinThread(thread);
        System.out.println("Component started:\n" + components.get(id).toString());
    }


    public boolean stopComponent(UUID id) {
        Thread thread = threads.get(id);
        boolean stopped = false;
        if (thread != null && !thread.isInterrupted()) {
            components.get(id).stopComponent();
            joinThread(thread);
            thread.interrupt();
            threads.put(id, new Thread(components.get(id)));
        }
        return stopped;
    }

    public boolean deleteComponent(UUID id) {
        Thread thread = threads.get(id);
        boolean disposable = false;
        if (thread != null) {
            joinThread(thread);
            disposable = this.components.get(id).deleteComponent();
            Component removedComponent = null;
            if (disposable) { // wenn Component Running, nicht löschbar
                thread.interrupt(); // Nur zur Sicherheit
                this.threads.remove(id);
                removedComponent = this.components.remove(id);
            }

            joinThread(thread);
            if (removedComponent != null) {
                System.out.println("Component deleted:\n" + removedComponent);
            }
        }
        return disposable;
    }

    private static void joinThread(Thread thread) {
        try {
            // Wait for the thread to finish any other previously called command
            thread.join();
        } catch (InterruptedException e) {
            System.out.println("Thread interrupted");
        }
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

    public boolean isModifyCache() {
        return modifyCache;
    }
}