package parser;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author tianzhangh 908333
 *
 */
public class Entry {
	
	private String word;
	private List<Definition> contents = new ArrayList<Definition>();;
	
	public Entry(String word, List<Definition> contents) {
		this.setWord(word);
		this.setContents(contents);
	}

	public String getWord() {
		return word;
	}

	public void setWord(String word) {
		this.word = word;
	}

	public List<Definition> getContents() {
		return contents;
	}

	public void setContents(List<Definition> contents) {
		this.contents = contents;
	}
}