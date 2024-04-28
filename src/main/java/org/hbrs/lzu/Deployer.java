package org.hbrs.lzu;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class Deployer {

    public void deployComponent(String jarPath) throws Exception {
        // Todo: for every component own thread

        JarFile jarFile = new JarFile(jarPath); // Runtime.Version to assure compatibility of loaded classes with current runtime?
        Enumeration<JarEntry> entry = jarFile.entries();
        URL[] urls = {new URL("jar:" + jarPath + "!/")};
            // URL cl für JARS oder dirs mit .class-Dateien
        URLClassLoader loader = URLClassLoader.newInstance(urls);
        try {

            while (entry.hasMoreElements()) {
                JarEntry e = entry.nextElement();
                if (e.isDirectory() || !e.getName().endsWith(".class")) {
                    continue;
                }
                String className = e.getName().substring(0, e.getName().length() - 6);
                className = className.replace('/', '.');
                Class<?> c = loader.loadClass(className);
                // Todo: Find starting class and store in component
                // if (c.isAnnotationPresent(Startable.class)) {
                // }
            }
        } catch (ClassNotFoundException e) {
            throw new Exception(e);
        } finally {
            jarFile.close();
            loader.close();
        }

    }
}
