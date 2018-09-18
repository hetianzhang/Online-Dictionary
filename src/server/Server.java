package server;


import java.io.BufferedReader;
import java.io.DataOutputStream;
//import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;

import javax.net.ServerSocketFactory;

import parser.Definition;
import parser.StreamDemo;

/**
 * 
 * @author tianzhangh 908333
 *
 */
public class Server {
	
	// Dictionary Controller of this Server
	private static StreamDemo streamDemo;
	
	// Declare the port number
	private static int port = 4000;
	private static String filepath;
	private static String filename; // default dictionary name for back-up
	private static String wordlist;
	private static String fullpath;
	
	// Identifies the user number connected
	private static int counter = 0;
	@SuppressWarnings("unused")
	private static int exceptionCounter = 0;
	
	//auto add word list from Oxford
	private static boolean autoadd = false;
	private static boolean online = false;
	private static long startTime;
	
	//declare function type
	private static Functype action;
	public enum Functype {
		QUERY(0), ADD(1), REMOVE(2), OTHERS(10);
		
		private int nCode;
		
		private Functype(int _nCode) {
			this.nCode = _nCode;
		}
		public String toString() {
			return String.valueOf(this.nCode);
		}
	}
	Server(){}
	Server(StreamDemo streamDemo){
		Server.streamDemo = streamDemo;
	}
	
	

	public static void main(String[] args) throws InterruptedException, IOException{
		

		//for test: start time
		startTime = System.currentTimeMillis();
		
		//create the controller of dictionary
		StreamDemo streamDemo = new StreamDemo();		
		new Server(streamDemo);
		
		//initialize Dictionary Configuration
		System.out.println("------------Intizaling Dictionary--------");
		filepath=System.getProperty("user.dir")+"\\";
		filename="xDictionary.txt"; // default dictionary name for back-up
		wordlist= filepath + "wordlist.txt";
		fullpath = filepath + filename;
		
		int length = args.length;
		if(length> 0 && length < 6) {
			System.out.println("setting server....");
			for(int n =0; n < length; n++) {
				if(n == 0) {
					int portnum = Integer.parseInt(args[0]);
					if(portnum <= 65535 && portnum >=1) //range of TCP port
						port = Integer.parseInt(args[0]); // set the socket listening port
				}
				if(n == 1) {
					fullpath = args[1]; // file absolute path
				}
				if(n == 2) {
					if(args[2].equalsIgnoreCase("online"))
						online = true;
					System.out.println("online model....");
				}
				if(n == 3) {
					if(args[3].equalsIgnoreCase("wordlist")) {
						autoadd = true;
						System.out.println("auto add model....");
					}
					if(length == 5)
						wordlist = args[4];
					else
						wordlist = System.getProperty("user.dir")+"\\wordlist.txt"; 
				}
			}
			
		}
				
		streamDemo.initialize();
		streamDemo.readDictionary(fullpath);

		
		// ThreadPool Initiation
		ServerSocketFactory factory = ServerSocketFactory.getDefault();
		//fixed thread pool: thread number is 2 cores: 4 threads (i7-7500u)
		ExecutorService executor = Executors.newFixedThreadPool(4);
		
		
		//update server dictionary with a word list
		final String wordlistpath = wordlist;
		if(autoadd == true)
		executor.submit(()->{
			try {
				autoAddfromOxford(wordlistpath);
			} catch (IOException | InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
		
		executor.submit(()->{
			try {
				backupService(fullpath);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
		
		
		try(ServerSocket server = factory.createServerSocket(port)){
			System.out.println("\nWaiting for client connection..");
			
			// Wait for connections.
			while(true){
				Socket client = server.accept();
				counter++;
				//System.out.println("\n>>Client request "+counter+": Applying for connection!");
				
				//thread for client serve
				executor.submit(()->serveClient(client));

			}
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (RejectedExecutionException e) {
			System.out.println(e.getMessage());
		} catch (NullPointerException e) {
			System.out.println(e.getMessage());
		}
		
		executor.shutdown();
		executor.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);

	}
	

	private static void backupService(final String filename) throws InterruptedException {
		int n = 0;
		String date ="";
		while(true) {			
			TimeUnit.MINUTES.sleep(2);			
			String currentdate = LocalDate.now().toString();
			if(currentdate.equals(date)== false) {
				n = 0;
				date = currentdate;
			}
			streamDemo.getDictionary().getVersion().setVersion(currentdate);
			streamDemo.storeDictionary("xDictionary" + date+ ".xml");
			System.out.println("Today's"+ n +" Times!");
			System.out.println("requests: " +counter);
			System.out.print(System.currentTimeMillis()-startTime);
			
			n++;
		}
	}
	private static void serveClient(Socket client){

		try(Socket clientSocket = client){
			
			String sentence = "";
			String fuctiontype = "";
			String word = "";
			String xml = "";

			// Input stream
			BufferedReader input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			// Output Stream
		    DataOutputStream output = new DataOutputStream(clientSocket.getOutputStream());
		    
		    //convert the contents to a Entry's Definition List after the words in the input string
		    sentence = input.readLine();
		    String[] words = sentence.split("#");
			List<Definition> list = new ArrayList<>();
			int i = 0;
			boolean deflag = false;
			boolean wordflag = false;
			boolean funcflag = false;
			
			while(i < words.length) {
			   if(i == 0) {
				    // Function type first
					fuctiontype = words[i];
				   
				    // present all available functions					
				    Functype[] allfuntype = Functype.values();
				    for (Functype type : allfuntype) {
				    	if(fuctiontype.equalsIgnoreCase(type.name()))
				    	{
				    	System.out.println("Find type: " + type.name() + type);
				    	action = type;
				    	funcflag = true;
				    	break;
				    	}	    	
				    }
				    if(funcflag == false) {
				    	action = Functype.OTHERS;
				    	xml = "ILLEGAL FUNC! ";
				    }
			   }
			   else if(i == 1) {
				   // Word second
					   wordflag = true;				   
			    	   word = words[i];
			    	   word = word.toLowerCase();
			   }
			   else{
				   if(words[i].length()!=0) {
					deflag = true;
					if(i+1<words.length)
						list.add(new Definition(words[i],words[i+1]));
					else
						list.add(new Definition(words[i],""));
				   }
			    	i++;
			    }
			    	i++;
			}
			//check flags
			if(wordflag == false & words.length<3){
				xml = xml + "No word Input! ";
				action =Functype.OTHERS;
			}
		    
		    /* the message defined as: the first word is action, 
			   the second is the word, the others are definitions and examples*/

		    //some check for the client input
		    System.out.println("CLIENT\nsentence: "+ sentence);
		    System.out.println("word: " + word);	    		    
		    System.out.println("Action: " + fuctiontype);
		    
		    switch(action){
		    	case ADD:
					if(deflag == false) 
						xml = xml + "No Definition!";
					else {
		    		    System.out.println("\n------an add request------");
		    		    xml = streamDemo.addEntry(word, list);    		    	
		    		   // System.out.println(xml);
					}
		    		break;
		    	case QUERY:
		    		System.out.println("\n------a query request-----");
		    		xml = streamDemo.query(word);
		    		if(online == true) {
		    			if(xml == null) 
		    				xml = streamDemo.addFromOxford(word);
		    		}
		    		if(xml == null)
		    			xml="404 Not Found!";
		    		//System.out.println(xml);		    		
				    break;
		    	case REMOVE:
		    		System.out.println("\n-------a remove request-----");
		    		if(streamDemo.remove(word))
		    			xml = "Been removed!";
		    		else
		    			xml = "Not exist!";		    		
		    		break;
		    	default:
		    		System.out.println("\n-------request error--------");
		    		break;
		    }	    
		    
		    output.writeBytes(xml);
		    output.flush();
		    client.close();
		    System.out.println("Disconnect the client"+ counter +" connection");
		} catch (IOException e) {
			e.printStackTrace();
			exceptionCounter++;
		}
	}
	
    private static void autoAddfromOxford(String wordfile) throws IOException, InterruptedException {
    	try(BufferedReader br= new BufferedReader(new FileReader(wordfile))){
    		String word = br.readLine();
    		int times = 0;
    		while(word != null && times < 2500) {
    			System.out.println(word);
    			String xml =streamDemo.query(word);
    			if(xml==null) {
    				streamDemo.addFromOxford(word);
    				TimeUnit.SECONDS.sleep(2);
    			}
    			word = br.readLine();
    			times++;
    		}
    	}
    }

}