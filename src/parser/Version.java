package parser;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

/**
 * 
 * @author tianzhangh 908333
 *
 */
public class Version {
	@XStreamAlias("name")
	@XStreamAsAttribute
	private String name;
	@XStreamAlias("author")
	@XStreamAsAttribute
	private String author;
	@XStreamAlias("version")
	@XStreamAsAttribute
	private String version;
	
	public Version(String name, String author, String version) {
		this.setName(name);
		this.setAuthor(author);
		this.setVersion(version);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}
}
