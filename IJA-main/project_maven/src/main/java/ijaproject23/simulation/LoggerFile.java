package ijaproject23.simulation;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class LoggerFile {
    //private static final Logger logger = Logger.getLogger(LoggerFile.class.getName());
    private File logFile;

    public LoggerFile() {
        logFile = new File("log.txt");
        try {
            if (!logFile.exists()) {
                logFile.createNewFile();
            } else {
                // If the file already exists, delete it and create a new one
                logFile.delete();
                logFile.createNewFile();
            }
        } catch (IOException e) {
            System.out.println("Error creating log file");
        }
    }

    public void info(String message) {
        try (FileWriter writer = new FileWriter(logFile, true)) {
            writer.write("INFO: " + message + "\n");
        } catch (IOException e) {
            System.out.println("Error writing to log file");
        }
    }

    public void warning(String message) {
        try (FileWriter writer = new FileWriter(logFile, true)) {
            writer.write("WARNING: " + message + "\n");
        } catch (IOException e) {
            System.out.println("Error writing to log file");
        }
    }

    public void severe(String message) {
        try (FileWriter writer = new FileWriter(logFile, true)) {
            writer.write("SEVERE: " + message + "\n");
        } catch (IOException e) {
            System.out.println("Error writing to log file");
        }
    }
}
