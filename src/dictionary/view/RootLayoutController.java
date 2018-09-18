package dictionary.view;

import java.io.File;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.FileChooser;
import dictionary.MainApp;

/**
 * 
 * @author tianzhangh 908333
 *
 */
public class RootLayoutController {

    // Reference to the main application
    private MainApp mainApp;
    
    public RootLayoutController() {
    }
    
    public void setMainApp(MainApp mainApp) {
    	this.mainApp = mainApp;
    }

    /**
     * Creates an word list
     */
    @FXML
    private void handleNew() {
        mainApp.getWordData().clear();
        mainApp.setWordListFilePath(null);
    }

    /**
     * Opens a FileChooser to let the user select a work list to load.
     */
    @FXML
    private void handleOpen() {
        FileChooser fileChooser = new FileChooser();

        // Set extension filter
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(
                "XML files (*.xml)", "*.xml");
        fileChooser.getExtensionFilters().add(extFilter);

        // Show open file dialog
        File file = fileChooser.showOpenDialog(mainApp.getPrimaryStage());

        if (file != null) {
            mainApp.loadWordListFromFile(file);
        }
    }

    /**
     * Saves the file to the word list file that is currently open. If there is no
     * open file, the "save as" dialog is shown.
     */
    @FXML
    private void handleSave() {
        File personFile = mainApp.getWordFilePath();
        if (personFile != null) {
            mainApp.saveWordListToFile(personFile);
        } else {
            handleSaveAs();
        }
    }

    /**
     * Opens a FileChooser to let the user select a file to save to.
     */
    @FXML
    private void handleSaveAs() {
        FileChooser fileChooser = new FileChooser();

        // Set extension filter
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(
                "XML files (*.xml)", "*.xml");
        fileChooser.getExtensionFilters().add(extFilter);

        // Show save file dialog
        File file = fileChooser.showSaveDialog(mainApp.getPrimaryStage());

        if (file != null) {
            // Make sure it has the correct extension
            if (!file.getPath().endsWith(".xml")) {
                file = new File(file.getPath() + ".xml");
            }
            mainApp.saveWordListToFile(file);
        }
    }

    /**
     * Opens the about dialog.
     */
    @FXML
    private void handleAbout() {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("xDictionary");
        alert.setHeaderText("About: COMP");
        alert.setContentText("Author: TianZhang He, 908333");

        alert.showAndWait();
    }

    /**
     * Closes the application.
     */
    @FXML
    private void handleExit() {
        System.exit(0);
    }
}