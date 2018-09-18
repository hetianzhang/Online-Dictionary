package parser;

import java.io.BufferedReader;
//import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
//import java.io.ObjectInputStream;
//import java.io.ObjectOutputStream;
//import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
//import java.nio.charset.Charset;
//import java.nio.charset.StandardCharsets;
//import java.nio.file.Files;
//import java.nio.file.Paths;
import java.util.*;
import java.util.Map.Entry;

import javax.net.ssl.HttpsURLConnection;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;

import com.thoughtworks.xstream.XStream;
//import com.thoughtworks.xstream.converters.extended.NamedMapConverter;

import parser.Dictionary;

/**
 * 
 * @author tianzhangh 908333
 *
 */

public class StreamDemo {
	
	private XStream xstream = new XStream();
	private Dictionary dc = null;
	//private ObjectOutputStream out = null;
	//private ObjectInputStream in = null;
	
	public synchronized void setDictionary(Dictionary dc) {
		this.dc = dc;
	}
	public synchronized Dictionary getDictionary() {
		return this.dc;
	}
    // for test the dictionary control part void main()
	/**
	public static void main(String[] args) throws IOException {
		
	List<Definition> c3 = new ArrayList<>();
		c3.add(new Definition("a small domesticated carnivore, Felis domestica or F. catus, "
				+ "bred in a number of varieties.",
				"I raised a cat before!"));
		String filepath="";
		String filename="";
		if(args.length>0 & args.length<3) {
			filepath=args[0];
			filename=args[1];
		}else {
			filepath= "C:\\\\Users\\\\tianzhangh\\\\eclipse-workspace\\\\DomParser\\\\src\\\\parser\\\\";
			filename= "text.txt";
		}
		
		//create the controller of dictionary
		StreamDemo streamDemo = new StreamDemo();
		
		streamDemo.initialize();
		streamDemo.setDictionary(streamDemo.testDictionary());
		streamDemo.storeDictionary(filepath+filename);
		streamDemo.readDictionary(filepath+filename);
		
		
		//Dictionary dc= streamDemo.testDictionary();
		//String xml = streamDemo.fromObjecttoMXL(dc);
		
		//create the dictionary from XML file first
		//streamDemo.fromXMLtoObject(xml);
		
		//from object to XML
		String xml = streamDemo.fromObjecttoMXL(streamDemo.getDictionary());
		//print the dictionary
		System.out.println(xml);
				
		//add a word (parser.Entry)
		System.out.println("\n----------ADD A WORD----------");
		System.out.println(streamDemo.addEntry("cat", c3));
		
		//query a word
		System.out.println("\n---------QUERY A WORD---------");
		xml = streamDemo.query("cat");
		System.out.println(xml);
		
		//remove a word
		System.out.println("\n---------REMOVE A WORD--------");
		String removeWord = "catty";
		if(streamDemo.remove(removeWord))
			System.out.println(removeWord + " has been removed!");
		else
			System.out.println("The Word doesn't exit in Dictionary!");
		
	}
	*/
	//test Demo Dictionary
	public Dictionary testDictionary() {
		//some contents for the demo dictionary UTF-8
		Version ver = new Version("dictionary-demo","tzhe","27-08-2017");
		Dictionary dc = new Dictionary(ver);
		List<Definition> c1 = new ArrayList<>();
		List<Definition> c2 = new ArrayList<>();
		c1.add(new Definition("heaven, as the final abode of the righteous.",
						"This place is just like a heaven!"));
		c1.add(new Definition("an intermediate place for the departed souls of the righteous awaiting resurrection.",
						" Christians have identified paradise both with the Garden of Eden and with heaven."));
		c2.add(new Definition("a domesticated canid, Canis familiaris, bred in many varieties.",
						"I am raising a dog!"));
		dc.put(new parser.Entry("paradise", c1));
		dc.put(new parser.Entry("dog", c2));
		return dc;	
	}
	
	//initialize dictionary from XML to in-memory Object
	public void initialize() {
		//setup security for XStream framework and initilizaiton
		Class<?>[] classes = new Class[] {Dictionary.class, Entry.class, Definition.class};
		XStream.setupDefaultSecurity(xstream);
		xstream.allowTypes(classes);
		// annotations part
		xstream.processAnnotations(Dictionary.class);
		xstream.alias("D", Definition.class);
		xstream.alias("Dictionary", Dictionary.class);
		
		// Map.Entry Indicates to key element, and if don't want to write key, need to be the value object class
		xstream.addImplicitMap(Dictionary.class, "hashmap", "s", Map.Entry.class, null);
		
		//NamedMapConverter namedMapConverter = new NamedMapConverter(xstream.getMapper(), "entry", "name", String.class, "list", List.class);
		//xstream.registerConverter(namedMapConverter);
		
		//xstream.addImplicitArray(Dictionary.class, "hashmap");
		
		xstream.aliasField("def", Definition.class, "defination");
		xstream.aliasField("eg", Definition.class, "example");
	}
	
	private synchronized void fromXMLtoObject(String xml) {	
		//from XML to object
		System.out.println("\n---------XML>>>>>Java Obj------------");
		dc = (Dictionary) xstream.fromXML(xml);
		System.out.println("xDictionary Author: " + dc.getVersion().getAuthor());
		System.out.println("Version:" + dc.getVersion().getVersion());
		//get map and set of the dictionary

		
	
	}
	public void iteratorDictionary() {		
		//iterate all entry
		Map<String, List<Definition>> map = dc.getContent();
		Set<Map.Entry<String, List<Definition>>> set = map.entrySet();
		Iterator<Map.Entry<String, List<Definition>>> i = set.iterator();
				
		while(i.hasNext()) {
		Map.Entry<String, List<Definition>> me = (Entry<String, List<Definition>>) i.next();
		System.out.println("-> word: " + me.getKey());
		List<Definition> deflist = me.getValue();
		Iterator<Definition> j = deflist.iterator();
		int num =0;
		while(j.hasNext()) {
			num++;
			Definition def = j.next();
			System.out.println("*Defition " + num + " : "+ def.getDefinations());
			System.out.println("*Example: " + def.getExamples());
		    }
	    }	
	}
	public synchronized void fromObjecttoXML(Dictionary dc) {
		//From object to XML
		System.out.println("\n--------Java Obj>>>>>XML-----------");
		String xml = xstream.toXML(dc);
		System.out.println(xml);
	}
	
	public synchronized String fromObjecttoMXL(Dictionary dc) {
		//From object to XML
		System.out.println("\n--------Java Obj>>>>>XML-----------");
		String xml = xstream.toXML(dc);
		return xml;		
	}
	

	//write dictionary XML to File
	public synchronized void storeDictionary(String filename) {
		String xml = fromObjecttoMXL(this.dc);
		try (PrintWriter out = new PrintWriter(filename, "UTF-8")) {			
			out.write(xml);
			System.out.println("\nDictionary Stored!");
			System.out.println("xDictionary version:"+dc.getVersion().getVersion());
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	//read XML file to string
	public synchronized void readDictionary(String filename) throws IOException {
		try(BufferedReader br = new BufferedReader(new FileReader(filename))) {
		    StringBuilder sb = new StringBuilder();
		    String line = br.readLine();

		    while (line != null) {
		        sb.append(line);
		        sb.append(System.lineSeparator());
		        line = br.readLine();
		    }
		    String xml = sb.toString();
		    this.fromXMLtoObject(xml);		    
		}
			
	}
	
	//======================================================
	//add an entry
	public String addEntry(String word, List<Definition> contents) {
		String xml = "";
		List<Definition> deflist = dc.put(new parser.Entry(word, contents));
		if(deflist == null)
		    xml ="success!";
		else
			xml= "Exist!";
		return xml;
	}
	
	//query an entry string version
	public String query(String word) {
		String xml = "";
		// thread-safe concurrent read 
		List<Definition> deflist = (List<Definition>)dc.get(word);
		if(deflist == null) return xml = null;
		Iterator<Definition> j = deflist.iterator();
		int num =0;
		while(j.hasNext()) {
			num++;
			Definition def = j.next();
			xml = xml + String.format("*Definition %d"+" : %s\n"+"*Example: %s\n",
					num,def.getDefinations(),def.getExamples());			
	    }
		return xml;	
	}
	
	//remove an entry
	public boolean remove(String word) {
		boolean flag = false;
		if(dc.remove(word) == null) return false; // the word doesn't exit
		else {
			flag = true;
		}
		return flag;		
	}
	
	//=============================================================
	//REST API request part get JSON
	public String addFromOxford(String word) {
		String xml ="";
		List<Definition> senses = this.jsonDefList(word);
		if(senses.isEmpty() == false) {
			this.addEntry(word, senses);
			xml =this.query(word);
			return xml;
		}
		else {
			return xml=null;
		}
	}
	private List<Definition> jsonDefList(String word) {
		List<Definition> senses = new ArrayList<>();
		String url = this.dictionaryEntries(word);
		String jsonfile = this.getJsonString(url);
		//print JSON file
		//System.out.println("Jsonfile: "+ jsonfile);
		
		//get senses from jsonfile
		if(jsonfile != null)
			senses = this.parseJason(jsonfile);
		return senses;
	}
	
	//test
	public String testRestApi(String word) {
		String jsonfile = this.getJsonString(this.dictionaryEntries(word));
		return jsonfile;
	}
	
	private List<Definition> parseJason(String jsonfile) {
		 // read the output Gson, Jackson, JSONP, org.json? jackson
		String definitions="";
		String examples="";
        ObjectMapper mapper = new ObjectMapper();
        
        try {
        	List<Definition> list = new ArrayList<>();
        	ObjectNode node = mapper.readValue(jsonfile, ObjectNode.class);
        	List<JsonNode> senselist = node.findValues("senses");
        	Iterator<JsonNode> s = senselist.iterator();
        	while(s.hasNext()) {
        		JsonNode senses = s.next();
        		if(senses.isArray())
        			System.out.println("find senses array!");
        		Iterator<JsonNode> se = senses.iterator();
        		while(se.hasNext()) {
        			JsonNode sense = se.next();
        			if(sense.has("definitions")) {
        			JsonNode def = sense.findValue("definitions");
        			System.out.println("definitions: " + def.toString());
        			definitions = def.toString();
        			}
        			if(sense.has("examples")) {
        			//JsonNode example = sense.findValue("examples");
        			System.out.println("Sentences: "+ sense.get("examples").toString());
        			examples = sense.get("examples").toString();
        			}
        			list.add(new Definition(definitions, examples));
        		}
        	}
			//List<Definition> list = mapper.readValue(jsonfile, new TypeReference<List<Definition>>() { });
			return list;
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
		
	}
	private String dictionaryEntries(String word) {
		final String language = "en";
		String url ="https://od-api.oxforddictionaries.com:443/api/v1/entries/" + language +"/" + word;
		return url;
	}
	private String getJsonString(String... params) {
		//TODO: own app id and app key
        final String app_id = "5303e9e5";
        final String app_key = "4d872060cde116839307b63ad355eaa2";
        try {
            URL url = new URL(params[0]);
            HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
            urlConnection.setRequestProperty("Accept","application/json");
            urlConnection.setRequestProperty("app_id",app_id);
            urlConnection.setRequestProperty("app_key",app_key);

            // read the output from the server
            BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            StringBuilder stringBuilder = new StringBuilder();

            String line = null;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line + "\n");
            }

            return stringBuilder.toString();

        }
        catch (Exception e) {
            //e.printStackTrace();
            System.out.println("404 NOT FOUND" + e.getMessage());
        }
		return null;
	}

}

