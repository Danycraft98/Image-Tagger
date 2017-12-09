package image;

import java.io.*;

public class Log {

    private String filename;

    // The constructor for Log.
    public Log(String logType) {
        this.filename = logType + " history.log";
    }

    /**
     * Add a line of log to Log File.
     *
     * @param line The log content
     */
    void addLog(String line) {
        try {
            FileWriter writer = new FileWriter(filename, true);
            writer.write(line + "\n");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns the Log File path.
     *
     * @return path
     */
    public String getPath() {
        return filename;
    }
}
