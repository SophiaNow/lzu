package org.hbrs.lzu;

import org.hbrs.lzu.state.Deployed;
import org.hbrs.lzu.state.State;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.nio.file.Files;
import java.util.UUID;

public class Component implements Runnable {
    private State state;
    private final URL url;
    private final UUID id;
    protected final Class<?> startingClass;
    private String name;

    public Component(UUID id, URL url, Class<?> startingClass, String name) {
        this.id = id;
        this.url = url;
        this.startingClass = startingClass;
        this.name = name;
        // this needs to be at the end of the constructor, so that all information can be cached
        setState(new Deployed(this));
    }

    @Override
    public void run() {
        try {
            this.init();
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public void init() throws InvocationTargetException, IllegalAccessException {
        this.state.init();
    }

    public void stopComponent() {
        try {
            this.state.stopComponent();
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean deleteComponent() {
        return this.state.deleteComponent();
    }

    public UUID getId() {
        return this.id;
    }

    public void setState(State state) {
        this.state = state;
        if (RuntimeEnvironment.getInstance().isModifyCache())
            cacheState();
    }

    private void cacheState() {
        String fileName = "cache.txt";
        try {
            File file = new File(fileName);
            file.createNewFile();
        } catch (IOException e) {
            System.err.println("File could not be created.");
            e.printStackTrace();
        }

        try {
            File inputFile = new File(fileName);
            File tempFile = new File("temp.txt");

            BufferedReader reader = new BufferedReader(new FileReader(inputFile));
            BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));

            String line;
            boolean replaced = false;
            StringBuilder contentBuilder = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                String[] lineInfo = line.split(",");
                if (getId().toString().equals(lineInfo[0])) {
                    // Replace the content of the line
                    writer.write(lineInfo[0] + "," + lineInfo[1] + "," + lineInfo[2] + "," + getState().getStateName());
                    replaced = true;
                } else {
                    // Keep the original line
                    writer.write(line);
                }
                writer.newLine(); // Add new line character after each line
            }

            // If not replaced, add a new entry
            if (!replaced) {
                writer.write(getId().toString() + "," + getName() + "," + url + "," + getState().getStateName());
                writer.newLine();
            }

            reader.close();
            writer.close();

            // Delete the original file
            Files.delete(inputFile.toPath());

            // Rename the temporary file to the original file name
            if (!tempFile.renameTo(inputFile)) {
                System.err.println("Failed to rename the temporary file.");
            }
        } catch (IOException e) {
            System.err.println("An error occurred while replacing the line in the file.");
            e.printStackTrace();
        }
    }

    public State getState() {
        return this.state;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public Class<?> getStartingClass() {
        return this.startingClass;
    }

    @Override
    public String toString() {
        String string = "[";
        string += this.name + ", ";
        string += this.id + ", ";
        String state = this.state.getClass().getName();
        string += state.substring(state.lastIndexOf('.') + 1) + "]";
        return string;
    }
}
