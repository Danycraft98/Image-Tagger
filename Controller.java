package application;

import image.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;

import javafx.scene.Scene;
import javafx.scene.canvas.*;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import javax.activation.MimetypesFileTypeMap;
import java.io.BufferedReader;
import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    // The root path user has selected.
    private String rootPath;

    // The current directory path of files that visible on the side bar.
    private String currentPath;

    // The file list side bar and all existing tag list side bar.
    @FXML
    private ListView<String> fileList, allTagList = new ListView<>();

    // The array of all existing tags in the log.
    private ArrayList<Tag> allTags = new ArrayList<>();

    // The button to move the file to another directory.
    @FXML
    private Button btnMoveTo = new Button();

    // The label to display current image path.
    @FXML
    public Label filePath;

    // The canvas to draw tags and image.
    @FXML
    public Canvas imageView = new Canvas();
    private GraphicsContext gc;

    // The image as TagManager.
    private TagManager tagManager;

    // The coordinates of mouse click on Canvas.
    private double x;
    private double y;

    // The components to add tag to the image.
    @FXML
    public TextField tagContent = new TextField();
    @FXML
    public Button btnSubmit = new Button();

    // The button to delete tags on an image.
    @FXML
    public Button btnDeleteTag = new Button();

    // The stage for displaying tags that appear on the image.
    private Stage deleteTagStage = new Stage();

    // The list of tags that are visible on deleteTagStage.
    @FXML
    private ListView<String> tagList = new ListView<>();
    private ObservableList<String> tags;

    // The button to revert to old names and tags.
    @FXML
    public Button btnTagHistory = new Button();

    // The stage for displaying past names of the image.
    private Stage tagHistoryStage = new Stage();

    // The list of past names that are visible on tagHistoryStage.
    @FXML
    private ListView<String> nameList = new ListView<>();

    // The stage for displaying name history.log and list of log lines.
    private Stage logStage = new Stage();
    @FXML
    private ListView<String> logList = new ListView<>();


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        getAllTags();

        // The initialization of GraphicsContext
        gc = imageView.getGraphicsContext2D();

        // Disable the textfield and buttons as no image is loaded.
        tagContent.setDisable(true);
        tagContent.setPromptText("Enter the tag content.");
        btnSubmit.setDisable(true);
        setButtons(tagManager == null);

        // The initialization of click event of allTagList.
        allTagList.setOnMouseClicked(e -> {
            if (imageView.getWidth() > 0) {
                int index = allTagList.getSelectionModel().getSelectedIndex();
                tagManager.addTag(allTags.get(index));
                tagManager.draw(gc);
                getSelectedFolder();
                filePath.setText(tagManager.getFilePath());
            }
        });

        // The initialization of click event of tagList.
        tagList.setOnMouseClicked(e -> {
            int index = tagList.getSelectionModel().getSelectedIndex();
            tagManager.deleteTag(index);
            tagManager.renameFile();

            tags.remove(index);
            tagManager.draw(gc);
            getSelectedFolder();
            filePath.setText(tagManager.getFilePath());
        });

        //The initialization of click event of nameList.
        nameList.setOnMouseClicked(e -> {
            int index = nameList.getSelectionModel().getSelectedIndex();
            tagManager.setTags(index - 1);
            tagManager.renameFile();
            tagManager.draw(gc);
            getSelectedFolder();
            filePath.setText(tagManager.getFilePath());
        });
    }

    // The event handler used to set the root directory.
    @FXML
    private void handleSetRootDirAction() {
        DirectoryChooser directoryChooser = new DirectoryChooser();

        // Show open file dialog
        File root = directoryChooser.showDialog(new Stage());

        // Check if the directory is chosen by DirectoryChooser.
        if (root != null) {
            rootPath = root.getPath();
            currentPath = rootPath;
            getSelectedFolder();
        }
    }

    // The event handler used to load image on fileList.
    @FXML
    private void handleLoadImageAction() throws IOException {
        String filename = fileList.getSelectionModel().getSelectedItem();
        if (rootPath != null && filename != null) {
            if (filename.equals("..")) {
                currentPath = new File(currentPath).getParent();
            } else {
                String filePath = currentPath.concat("/" + filename);
                if (new File(filePath).listFiles() == null) {
                    //Create TagManager for the image file.
                    tagManager = new TagManager(filePath);
                    tagManager.draw(gc);
                    setButtons(tagManager == null);
                    this.filePath.setText(tagManager.getFilePath());
                } else {
                    currentPath = filePath;
                }
            }
        }
        getSelectedFolder();
    }

    // The event handler used to get mouse click image.
    @FXML
    private void handleImageClickAction(MouseEvent event) {
        tagContent.setDisable(false);
        tagContent.requestFocus();
        btnSubmit.setDisable(false);
        x = event.getX();
        y = event.getY();
        getSelectedFolder();
    }

    // The event handler used to submit tag.
    @FXML
    private void handleSubmitTagAction() {
        if ((tagContent.getText() != null && !tagContent.getText().isEmpty())) {
            tagManager.addTag(new Tag("@" + tagContent.getText(), x, y));
            tagManager.draw(gc);
            tagContent.setDisable(true);
            btnSubmit.setDisable(true);
            tagContent.clear();
            getSelectedFolder();
            getAllTags();
            filePath.setText(tagManager.getFilePath());
        }
    }

    // The event handler to display the deleteTagStage.
    @FXML
    private void handleDeleteTagAction() throws IOException {
        FXMLLoader deleteTagLoader = new FXMLLoader(getClass().getResource("../viewFiles/deleteTag.fxml"));
        deleteTagLoader.setController(this);
        deleteTagStage.setScene(new Scene(deleteTagLoader.load(), 250, 300));

        tags = FXCollections.observableArrayList();
        tagList.getSelectionModel().clearSelection();

        for (Tag tag : tagManager.getTags()) {
            tags.add(tag.getContent());
        }
        tagList.setItems(tags);
        deleteTagStage.show();
    }

    // The event handler to display the tagHistoryStage.
    @FXML
    private void handleTagHistory() throws IOException {
        FXMLLoader tagHistoryLoader = new FXMLLoader(getClass().getResource("../viewFiles/tagHistory.fxml"));
        tagHistoryLoader.setController(this);
        tagHistoryStage.setScene(new Scene(tagHistoryLoader.load(), 250, 300));

        ObservableList<String> names = FXCollections.observableArrayList();
        nameList.getSelectionModel().clearSelection();

        names.addAll(tagManager.getPastNames());
        nameList.setItems(names);
        tagHistoryStage.show();
    }

    // The event handler to move the image file.
    @FXML
    private void handleMoveTagAction() {
        DirectoryChooser directoryChooser = new DirectoryChooser();

        // Show open file dialog
        File file = directoryChooser.showDialog(new Stage());

        // Check if the directory is chosen by DirectoryChooser.
        if (file != null) {
            // Moves the image to selected path.
            tagManager.moveFile(file.getPath());

            // Makes the image "disappear" on application screen.
            setButtons(true);
            imageView.setHeight(0);
            imageView.setWidth(0);
            getSelectedFolder();
            filePath.setText("");
        }
    }

    // The event handler to display the logStage.
    @FXML
    private void handleOpenLogAction() throws IOException {
        FXMLLoader logLoader = new FXMLLoader(getClass().getResource("../viewFiles/log.fxml"));
        logLoader.setController(this);
        logStage.setScene(new Scene(logLoader.load()));
        logStage.setMaxHeight(400);
        logStage.setMaxWidth(500);

        String nameLogPath = new Log("name").getPath();
        if (new File(nameLogPath).exists()) {
            ObservableList<String> lines = FXCollections.observableArrayList();

            BufferedReader br = new BufferedReader(new FileReader(nameLogPath));
            String line;
            while ((line = br.readLine()) != null) {
                lines.add(line);
            }
            logList.setItems(lines);

        }
        logStage.show();
    }

    // Get the files and folders of selected folder.
    private void getSelectedFolder() {
        File[] subfiles = new File(currentPath).listFiles();
        if (subfiles != null) {
            ObservableList<String> files = FXCollections.observableArrayList();
            if (!currentPath.equals(rootPath)) {
                files.add("..");
            }
            for (File file : subfiles) {
                if (new MimetypesFileTypeMap().getContentType(file).contains("image") || file.listFiles() != null) {
                    files.add(file.getName());
                }
            }
            fileList.setItems(files);
        }
    }

    // Get all the tags in the tag history.log.
    private void getAllTags() {
        String tagLogPath = new Log("tag").getPath();
        ObservableList<String> allTagContents = FXCollections.observableArrayList();
        if (new File(tagLogPath).exists()) {
            try {
                BufferedReader br = new BufferedReader(new FileReader(tagLogPath));
                String line;
                while ((line = br.readLine()) != null) {
                    List<String> tags = Arrays.asList(line.split(">"));
                    for (int i = 2; i < tags.size(); i++) {
                        String[] tagInfo = tags.get(i).split(",");
                        Tag tag = new Tag(tagInfo[0], Double.parseDouble(tagInfo[1]), Double.parseDouble(tagInfo[2]));
                        if (!allTags.contains(tag)) {
                            allTags.add(tag);
                            allTagContents.add(tagInfo[0]);
                        }
                    }
                }
                allTagList.setItems(allTagContents);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Set the buttons disabled based on "disabled"
    private void setButtons(boolean disabled) {
        btnDeleteTag.setDisable(disabled);
        btnTagHistory.setDisable(disabled);
        btnMoveTo.setDisable(disabled);
    }

}