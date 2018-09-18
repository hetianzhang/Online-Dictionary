package parser;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import parser.Definition;
import parser.Entry;
import parser.Version;

/**
 * 
 * @author tianzhangh 908333
 *
 */
public class Dictionary {
	private Version version;
	
	//ConcurrentHashMap: initialCapacity, loadFactor and concurrencyLevel
	private final Map<String, List<Definition>> hashmap = new ConcurrentHashMap<>();
	//private Map<String, List<Definition>> synhashmap = Collections.synchronizedMap(hashmap);
	
	public Dictionary(){
		
	}
	public Dictionary(Version version) {
		this.setVersion(version);
	}
	public List<Definition> get(String word) {
		return hashmap.get(word); //thread safe read
	}
	
    public List<Definition> put(Entry word) {
    	return hashmap.putIfAbsent(word.getWord(), word.getContents()); // return null if not exist currently
	}
	public List<Definition> remove(String word) {
		if(hashmap.containsKey(word))
			return hashmap.remove(word); // remove exist
		else
			return null; // not exist
			
	}
	public Map<String, List<Definition>> getContent() {
		return this.hashmap;
	}

	public synchronized Version getVersion() {
		return version;
	}

	public synchronized void setVersion(Version version) {
		this.version = version;
	}
	

	
	
	

}

