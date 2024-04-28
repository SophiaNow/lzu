package org.hbrs.lzu;

import java.io.IOException;

public class RuntimeEnvironment {
    private Process process;

    public void start() throws IOException {
        ProcessBuilder processBuilder = new ProcessBuilder("java", "-jar", "YourApplication.jar");
        this.process = processBuilder.start();
    }

    public boolean stop() {
        if (this.process == null) {
            return false;
        }
        this.process.destroy();
        return true;
    }
    // Todo: deploy, start, stop, delete Component
}
