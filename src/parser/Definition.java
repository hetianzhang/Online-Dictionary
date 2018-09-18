package parser;

/**
 * 
 * @author tianzhangh 908333
 *
 */
public class Definition {
	private String definations;
	private String examples;
	
	public Definition(String defination, String example){
		this.setDefinations(defination);
		this.setExamples(example);
	}

	public String getDefinations() {
		return definations;
	}

	public void setDefinations(String definations) {
		this.definations = definations;
	}

	public String getExamples() {
		return examples;
	}

	public void setExamples(String examples) {
		this.examples = examples;
	}

}
