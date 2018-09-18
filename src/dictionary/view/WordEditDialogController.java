package dictionary.view;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import dictionary.model.*;
import client.Client;
public class WordEditDialogController {
	/**
	 * Dialog to add a word and its senses(definitions)
	 * 
	 * @author TianZhang He
	 */
	@FXML
	private TextField wordNameField;
	@FXML
	private TextArea sensesArea;
	
	private Stage dialogStage;
	private Word word;
	private boolean okClicked = false;
	
	@FXML
	private void initialize() {
		
	}
	
	public void setDialogStage(Stage dialogStage) {
		this.dialogStage = dialogStage;
	}
	
	public void setWord(Word word) {
		this.word = word;
		//client local word
		wordNameField.setText(word.getWordName());
		sensesArea.setText(word.getSenses());		
	}
	
	public boolean isOKclicked() {
		return okClicked;
	}
	
	@FXML
	private void handleOK() {
		if(isInputValid()) {
			
			//add local client word
			word.setWordName(wordNameField.getText());
			word.setSenses(sensesArea.getText());
			
			this.okClicked = true;
			dialogStage.close();
		}
	}
	@FXML
	private void handleCancel() {
		dialogStage.close();
	}
	@FXML
	private void handleSubmit() {
		if(isInputValid()) {
			String funcType ="ADD";
			String wordName = wordNameField.getText();
			String senses = sensesArea.getText();
			String request = funcType+ "#"+wordName+senses;
			Client client = new Client();
			String[] requests = request.split("\n");
			int i = 0;
			request = "";
			while(i < requests.length) {
				request += requests[i];
				i++;
			}			
			String xml = client.request(request);
			sensesArea.setText(xml);
			
		}
	}
	
	private boolean isInputValid() {
		String errorMessage = "";
		if(wordNameField.getText() == null || wordNameField.getText().length() == 0) {
			errorMessage += "No valid word!\n";
		}
		if(sensesArea.getText() == null || sensesArea.getText().length() == 0) {
			errorMessage += "Empty Definitions!\n";
		}else {
			if(sensesArea.getText(0, 1).equals("#")) {
			String[] sentences = sensesArea.getText().split("#");
			int i =0;
			while(i< sentences.length) {
				if(i==0) {
					if(sentences[0]==null)
						errorMessage +="No valid Definitions!\n";
				}
				i += 2;
			}
			}
			else {
				errorMessage += "Invalid format!\n";
			}
		}
		if(errorMessage.length() == 0)
			return true;
		else {
			Alert alert = new Alert(AlertType.ERROR);
			alert.initOwner(this.dialogStage);
			alert.setTitle("Invalid Fields");
			alert.setHeaderText("Please correct invalid fields");
			alert.setContentText(errorMessage);
			
			alert.showAndWait();
			return false;
		}
	}
}
