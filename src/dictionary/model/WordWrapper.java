package dictionary.model;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
/**
 * 
 * @author tianzhangh 908333
 *
 */
@XmlRootElement(name = "words")
public class WordWrapper {
	private List<Word> words;
	
	@XmlElement(name = "word")
	public List<Word> getWords(){
		return this.words;
	}
	public void setWords(List<Word> words) {
		this.words = words;
	}

}
