package dictionary.model;


import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * 
 * @author tianzhangh 908333
 *
 */
public class Word {
	/**Model class for client word cache
	 * 
	 * @author TianZhang He
	 * */
	private final StringProperty wordname;
	private final StringProperty senses; 
	
	public Word() {
		this(null,null);
	}
	public Word(String word, String senses) {
		this.wordname = new SimpleStringProperty(word);
		this.senses = new SimpleStringProperty(senses);
	}
	
	public String getWordName() {
		return this.wordname.get();
	}
	
	public StringProperty wordNameProperty() {
		return this.wordname;
	}
	
	public void setWordName(String wordname) {
		this.wordname.set(wordname);
	}
	
	public String getSenses() {
		return this.senses.get();
	}
	
	public StringProperty sensesProperty() {
		return this.senses;
	}
	public void setSenses(String senses) {
		this.senses.set(senses);;
	}

}
