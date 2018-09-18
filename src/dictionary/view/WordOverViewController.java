package dictionary.view;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import dictionary.MainApp;
import dictionary.model.*;

import java.util.Optional;

/**
 * 
 * @author tianzhangh 908333
 *
 */
public class WordOverViewController {
	@FXML
	private TableView<Word> wordList;
	
	@FXML
	private TableColumn<Word, String> wordNameColumn;
	@FXML
	private TableColumn<Word, String> sensesColumn;

	@FXML
	private Label wordNameLabel;
	
	@FXML
	private TextField searchField;
	
	@FXML
	private TextArea definitionsArea;
	
	//reference to the main application.
	private MainApp mainApp;
	
	public WordOverViewController() {
		
	}
	
	//initializes the controller class. automatically called after the FXML file has been loaded 
	// note: initialize not initialized save your day!
	
	@FXML
	private void initialize() {
		// Initialize the word list
		wordNameColumn.setCellValueFactory(cellValue->cellValue.getValue().wordNameProperty());
		sensesColumn.setCellValueFactory(cellValue->cellValue.getValue().sensesProperty());
		
		//clear
		showWordDifinitions(null);
		
		//listener
		wordList.getSelectionModel().selectedItemProperty().addListener((obervable, oldValue, newValue)-> showWordDifinitions(newValue));
	}
	
	public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;
		
		wordList.setItems(mainApp.getWordData());
	}
	
	private void showWordDifinitions(Word word) {
		if(word != null) {
			wordNameLabel.setText(word.getWordName());
		    definitionsArea.setText(word.getSenses());
		}else {
			wordNameLabel.setText("");
			definitionsArea.setText("");
		}
	}
	/**
	 * Called when the user clicks on the delete button.
	 */
	private boolean isRemoveOK(String word) {
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.initOwner(mainApp.getPrimaryStage());
		alert.setTitle("Remove");
		alert.setHeaderText("Confirmation");
		alert.setContentText("Are you sure to remove \""+ word +"\" ?");
		
		Optional<ButtonType> result = alert.showAndWait();
		if (result.get() == ButtonType.OK){
		    // ... user chose OK
			return true;
		} else {
		    // ... user chose CANCEL or closed the dialog
			return false;
		}
	}
	@FXML
	private void handleRemoveWord() {
		String word = this.searchField.getText();
		if(word.length()!=0)
		{
		boolean removeFlage = isRemoveOK(word);
		if(removeFlage==true) {
			String request = "REMOVE#"+word;
		    String xml = mainApp.getClient().request(request);
		    wordNameLabel.setText(word);
		    definitionsArea.setText(xml);
		}
		}
	}
	@FXML
	private void handleSearchPress() {
		searchField.setOnKeyPressed((event)->{
			if(event.getCode()==KeyCode.ENTER)
				this.handleQueryWord();
				});
	}
	@FXML
	private void handleQueryWord() {
		String word = this.searchField.getText();
		if(word.length()!=0) {
		String request = "QUERY#"+word;
		String xml = mainApp.getClient().request(request);
		//get the query results in the local cache
		mainApp.getWordData().add(new Word(word, xml));
		wordNameLabel.setText(word);
		definitionsArea.setText(xml);
		}
	}
	@FXML
	private void handleDeleteWord() {
	    int selectedIndex = wordList.getSelectionModel().getSelectedIndex();
	    if(selectedIndex>=0)
	    wordList.getItems().remove(selectedIndex);
	    else {
	    	//Nothing selected
	    	Alert alert = new Alert(AlertType.WARNING);
	    	alert.initOwner(mainApp.getPrimaryStage());
	    	alert.setTitle("No selection");
	    	alert.setHeaderText("No Word Selected!");
	    	alert.setContentText("Please select a person in the table.");
	    	
	    	alert.showAndWait();
	    	
	    }
	}
	/**
	 * Called when the user clicks the new button. Opens a dialog to edit
	 * details for a new word.
	 */
	@FXML
	private void handleNewWord() {
	    Word tempWord = new Word();
	    boolean okClicked = mainApp.showWordEditDialog(tempWord);
	    if (okClicked) {
	        mainApp.getWordData().add(tempWord);
	    }
	}
	
	/**
	 * Called when the user clicks the edit button. Opens a dialog to edit
	 * details for the selected person.
	 */
	@FXML
	private void handleEditWord() {
	    Word selectedWord = wordList.getSelectionModel().getSelectedItem();
	    if (selectedWord != null) {
	        boolean okClicked = mainApp.showWordEditDialog(selectedWord);
	        if (okClicked) {
	            this.showWordDifinitions(selectedWord);
	        }

	    } else {
	        // Nothing selected.
	        Alert alert = new Alert(AlertType.WARNING);
	        alert.initOwner(mainApp.getPrimaryStage());
	        alert.setTitle("No Selection");
	        alert.setHeaderText("No Word Selected");
	        alert.setContentText("Please select a word in the table.");

	        alert.showAndWait();
	    }
	}
}
