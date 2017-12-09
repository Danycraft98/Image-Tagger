# Image-Tagger
An application used to tag an image.

## To run the application using terminal
1. type "javac application/*.java image/*.java"
2. type "java application.Main"

## Classes

### Controller

#### Description:
The Controller.class used to handle all EventHandler.

### Log

#### Description:
The Log.class creates and documents all changes to the files.

#### Attributes:
- String path: the path to the Log file.

#### Methods:
- addLog(String line): adds a line of log to Log file.
- getPath(): returns path of log.

### TagManager

#### Description:
The TagManager.class represents the image file and its tags.

#### Attributes:
- File file: Image files
- String name: The original name of image file without extension.
- ArrayList<Tag> tags: All tags and its coordinates.
- Log nameLog, tagLog: Two log files.

#### Methods:
- addTag(Tag tag): add tag to tags.
- deleteTag(int index): deletes tag in tags.
- setTags(int searchIndex): setTag based on tag set on tagLog.
- renameFile(): if tags.size() > 0, then name is based on the tagNames. Else, the name is the original name.
- moveFile(String directory): move the image file to the given directory.
- draw(GraphicsContext gc): draws the tags on the image.
- logChange(String oldName, String newName): logs the changes in tags and names.
- getPastNames(): get the past names of the image file.
- getTags(): get all the existing tags.

### Tag

#### Description:
The Tag.class represents one tag on an image.

#### Attributes:
- String content: the content of tag.
- int x: the horizontal coordinate of tag on image.
- int y: the vertical coordinate of tag on image.

#### Methods:
- equals(Object object): checks if object is equal to Tag
- getContent(): returns the content of tag (String).
- getX(): returns the x-coordinate.
- getY(): returns the y-coordinate.
