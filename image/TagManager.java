package image;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

import javax.imageio.ImageIO;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TagManager {

    // Image file and name without tags.
    private File file;
    private String name;

    // ArrayList of Tags
    private ArrayList<Tag> tags;

    // The two different log files.
    private Log nameLog, tagLog;

    /**
     * The constructor of TagManager
     *
     * @param filePath The path of the file.
     */
    public TagManager(String filePath) {
        file = new File(filePath);

        String originalName = file.getName();
        if (originalName.contains("@")) {
            name = originalName.substring(0, originalName.indexOf("@") - 1);
        } else {
            name = originalName.substring(0, originalName.indexOf("."));
        }

        nameLog = new Log("name");
        tagLog = new Log("tag");
        setTags(-2);
    }

    /**
     * Add a Tag to an image.
     *
     * @param tag Tag tag being added.
     */
    public void addTag(Tag tag) {
        if (!tags.contains(tag)) {
            tags.add(tag);
            renameFile();
        }
    }

    /**
     * Delete a tag on the image.
     *
     * @param index The index to find the Tag being deleted
     */
    public void deleteTag(int index) {
        tags.remove(index);
    }

    /**
     * Sets up the tags from existing sets from tags log.
     * If searchIndex >=  0: then the tags will be one of the past tags set.
     * If searchIndex == -1: then the tags will be an empty set ie image with no tag.
     * If searchIndex == -2: then the tags will be the most recent tags set.
     *
     * @param searchIndex The index to find the set of Tags.
     */
    public void setTags(int searchIndex) {
        tags = new ArrayList<>();
        if (searchIndex != -1 && new File(tagLog.getPath()).exists()) {
            try {
                BufferedReader br = new BufferedReader(new FileReader(tagLog.getPath()));
                String line, lastLine = "";
                int index = 0;
                while ((line = br.readLine()) != null) {
                    if (searchIndex == index) {
                        break;
                    } else if (line.split(">")[1].equals(name)) {
                        lastLine = line;
                        index++;
                    }
                }
                if (line == null) {
                    line = lastLine;
                }
                List<String> tags = Arrays.asList(line.split(">"));
                for (int i = 2; i < tags.size(); i++) {
                    String[] tagInfo = tags.get(i).split(",");
                    this.tags.add(new Tag(tagInfo[0], Double.parseDouble(tagInfo[1]), Double.parseDouble(tagInfo[2])));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Renames the image file based on the tags on the image.
     */
    public void renameFile() {
        String oldName = file.getName();
        String newName = file.getParent() + "/" + name;
        for (Tag tag : tags) {
            newName = newName.concat(" " + tag.getContent());
        }
        File newFile = new File(newName.concat(oldName.substring(oldName.indexOf("."), oldName.length())));
        if (!file.renameTo(newFile)) {
            System.out.println("The file could not be renamed.");
        }
        file = newFile;
        logChange(oldName, file.getName());
    }

    /**
     * Moves the image file based on the directory.
     *
     * @param directory The directory to move image.
     */
    public void moveFile(String directory) {
        File newFile = new File(directory + "/" + file.getName());
        if (!file.renameTo(newFile)) {
            System.out.println("The file could not be moved.");
        }
        file = newFile;
    }

    /**
     * Draw the image and tags on to GraphicsContext
     *
     * @param gc The GraphicsContext.
     */
    public void draw(GraphicsContext gc) {
        try {
            Image image = SwingFXUtils.toFXImage(ImageIO.read(file), null);
            double side = (650 / image.getHeight()) * image.getWidth();
            if (side <= 950) {
                gc.getCanvas().setHeight(650);
                gc.getCanvas().setWidth(side);
                gc.drawImage(image, 0, 0, side, 650);
            } else {
                side = (950 / image.getWidth()) * image.getHeight();
                gc.getCanvas().setHeight(side);
                gc.getCanvas().setWidth(950);
                gc.drawImage(image, 0, 0, 950, side);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        for (Tag tag : tags) {
            gc.fillText(tag.getContent(), tag.getX(), tag.getY());
        }
    }

    /**
     * Log changes to both nameLog and tagLog.
     *
     * @param oldName The old name of file before change.
     * @param newName The old name of file after change.
     */
    private void logChange(String oldName, String newName) {
        String timestamp = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date());
        String message = timestamp + " " + oldName + " changed to " + newName;
        nameLog.addLog(message);

        message = timestamp + ">" + name;
        for (Tag tag : tags) {
            message = message.concat(">" + tag.getContent() + "," + tag.getX() + "," + tag.getY());
        }
        tagLog.addLog(message);
    }

    /**
     * Get past names from the nameLog.
     *
     * @return names
     */
    public ArrayList<String> getPastNames() {
        ArrayList<String> names = new ArrayList<>();
        if (new File(nameLog.getPath()).exists()) {
            try {
                BufferedReader br = new BufferedReader(new FileReader(nameLog.getPath()));
                String line;

                Pattern p = Pattern.compile("(?<name>[a-zA-Z]+)(?: @[a-zA-Z]+)*\\.[a-z]+");
                while ((line = br.readLine()) != null) {
                    Matcher m = p.matcher(line);
                    if (m.find() && m.group("name").equals(name)) {
                        names.add(m.group());
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return names;
    }

    /**
     * Get the tags on the image.
     *
     * @return tags
     */
    public ArrayList<Tag> getTags() {
        return tags;
    }

    /**
     * Get the image file.
     *
     * @return filePath
     */
    public String getFilePath() {
        return file.getPath();
    }
}
