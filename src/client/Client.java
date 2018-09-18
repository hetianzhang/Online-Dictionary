package client;

import java.io.BufferedReader;
//import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.net.Socket;
//import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.ThreadPoolExecutor;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeUnit;

/***
 * 
 * @author tianzhangh 908333
 *
 */

public class Client {
	
	// IP and port belong to the CLient Class
	private static String ip = "localhost";
	private static int port = 4000;
	private static int test = 0;
	
	// for the test
	private static long totaltime =0;
    private static long totalupdate =0;
    private static long totalremove =0;
    private static int readcount =0;
    private static int exception =0;
    private static int updatecount =0;
    private static int removecount=0;
    private static long maxReadTime=0;
    private static long oldReadTime=0;
    private static long maxUpdateTime=0;
    private static long oldUpdateTime=0;
	
	public Client() {
	}
	
	@SuppressWarnings("static-access")
	public Client(int port, String ip) {
		this.ip = ip;
		this.port = port;
	}
	
	//need the request string without \n, otherwise a IO exception will occur
	public String request(String request) {
		String sentences="";
		String xml ="";
		if(request.length()!=0) {
		try(Socket socket = new Socket(ip, port);){
			
			// Output and Input Stream
			BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		    DataOutputStream output = new DataOutputStream(socket.getOutputStream());
			
		    if(socket.isConnected())
		    	output.writeBytes(request + '\n');
		    else{
		    	System.out.println("Disconnected!");
		    	socket.close();
		    }
		    
		    while((xml = input.readLine()) != null) {
		    	if(xml.isEmpty())
		    		break;	    	
		    	sentences = sentences + xml +"\n";		    	
		    }
		   
		    
		    socket.close();
		    

		    if(socket.isClosed()!= true)
		    	System.out.println("In connection");
		    else {
		    	System.out.println("Disconnected");
		    	}

		    return sentences;
		    
		}catch(ConnectException e) {
			//e.printStackTrace();
			System.out.println(e.getMessage());
		    exception++;
		}
		catch(SocketTimeoutException e) {
			//e.printStackTrace();
			System.out.println(e.getMessage());
		}
		catch (UnknownHostException e) {
			//e.printStackTrace();
			System.out.println(e.getMessage());
		} catch (IOException e) { // connection refused
			//e.printStackTrace();
			System.out.println(e.getMessage());
		}
		}
		return xml;
	}
	public static void testReadConcurrency(String word) {
			String sentence = null;
	    	long startTime;
			Client client = new Client();

				startTime = System.currentTimeMillis();
				sentence = client.request("query#"+word);
				totaltime += System.currentTimeMillis() - startTime;
				oldReadTime = System.currentTimeMillis() - startTime;	
				if(maxReadTime < oldReadTime)
					maxReadTime = oldReadTime;
	}
	public static void testAddConcurrency(String word, String senses) {
			Client client = new Client();
			String sentence = null;
			long startTime;
			startTime = System.currentTimeMillis();
			sentence = client.request("add#"+word+senses);
			totalupdate += System.currentTimeMillis() - startTime;
			oldUpdateTime = System.currentTimeMillis() - startTime;
			if(maxUpdateTime < oldUpdateTime)
				maxUpdateTime = oldUpdateTime; 
	}
	public static void testRemoveConcurrency(String word) {
		Client client = new Client();
		String sentence = null;
		long startTime;

		startTime = System.currentTimeMillis();
		sentence = client.request("remove#"+word);
		
		totalremove += System.currentTimeMillis() - startTime;
	}

    
	public static void main(String[] args) {
		if(args.length>0) {
			port = Integer.parseInt(args[0]);
			ip = args[1];
			if(args[2].equalsIgnoreCase("test")) {
				test =1;
			}
			
		}
		Client client = new Client();
		String sentence = null;
		BufferedReader userin = 
		new BufferedReader(
		new InputStreamReader(System.in));
		while(true) {

        System.out.println("QUERY/ADD/REMOVE a word:");
        try {
			sentence = userin.readLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        String senses= client.request(sentence);
    	System.out.println(senses);
        //for concurrent test
        while(test==1) {
        	senses= client.request(sentence);
        	System.out.println(senses);
        }
		}
		//experiments part
		/*List<String> words = new ArrayList<>();
		
		String testwordfile = "C:\\Users\\tianzhangh\\eclipse-workspace\\DomParser\\testwordlist.txt"; // 100 words list
    	try(BufferedReader br= new BufferedReader(new FileReader(testwordfile))){
    		String word = br.readLine();
    		while(word != null) {
    			words.add(word);
    			word =br.readLine();
    		}
    	} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
		try{

		    ExecutorService executor = Executors.newCachedThreadPool();
		    int flag = 0;
		    
		if(flag == 0) {
		PrintWriter readwriter = new PrintWriter("readdata2.txt", "UTF-8");
        for(int i=0;i<1500; i++) {
			try {
				TimeUnit.MILLISECONDS.sleep(10);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			int random = ThreadLocalRandom.current().nextInt(0, 100+1); // random
			String word = words.get(random);
			readcount++;
			executor.submit(()->testReadConcurrency(word));
			readwriter.println(totaltime+" " +(readcount-exception)+" "+ totaltime/(readcount-exception));
			System.out.println(exception);
		}
        readwriter.close();
        System.out.println("maxread response:" + maxReadTime);
		}
		else if(flag ==1)
		{
		PrintWriter updatewriter = new PrintWriter("updatedata2.txt", "UTF-8");
		for(int i=0; i<30000; i++) {
			try {
				TimeUnit.MILLISECONDS.sleep(1);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			updatecount++;
		    int randomupdate = ThreadLocalRandom.current().nextInt(0, 1499+1); // random
		    String updateword = String.valueOf(randomupdate);
		    	String updatesence = "#"+updateword;
		    	executor.submit(()->testAddConcurrency(updateword, updatesence));
		    	updatewriter.println(totalupdate+" "+(updatecount-exception)+" "+ totalupdate/(updatecount-exception));
		    	System.out.println(exception);
		}
		
		updatewriter.close();
		System.out.println("maxupdate response:" + maxUpdateTime);
		}
		else if(flag ==2)
		{
		PrintWriter updatewriter = new PrintWriter("removedata.txt", "UTF-8");
		for(int i=0; i<600; i++) {
			try {
				TimeUnit.MILLISECONDS.sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			removecount++;
		    int randomremove = ThreadLocalRandom.current().nextInt(0, 1499+1); // random
		    String removeword = String.valueOf(randomremove);
		    	executor.submit(()->testRemoveConcurrency(removeword));
		    	updatewriter.println(totalremove+" "+(removecount-exception)+" "+ totalremove/(removecount-exception));
		    	System.out.println(exception);
		}
		
		updatewriter.close();
		System.out.println("maxupdate response:" + maxUpdateTime);
		}
	    
		} catch (IOException e) {
		   e.printStackTrace();
		}    
		*/
		    
	}

}

