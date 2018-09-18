package dictionary;

import java.io.BufferedReader;
import java.io.File;
//import java.io.FileNotFoundException;
//import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
////import java.io.PrintWriter;
//import java.io.UnsupportedEncodingException;
//import java.util.Map.Entry;
import java.util.prefs.Preferences;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;


import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import dictionary.model.*;
import dictionary.view.*;
import client.Client;
/**
 * 
 * 
 * @author TianZhang He 908333
 *
 */
public class MainApp extends Application {

    private Stage primaryStage;
    private BorderPane rootLayout;
    
    // client type 0 for gui, 1 for command
    
    private static int clientType = 0;
    private static String ip = "localhost";
    private static int port = 4000;
    private  Client client;

    private ObservableList<Word> wordData = FXCollections.observableArrayList();
    
    public MainApp() {
    	//add some sample data for test
    	this.client = new Client(port, ip);
    	wordData.add(new Word("paradise","*Definitions1: heaven\n*Examples:This place is just like a paradise!\n"));
    	wordData.add(new Word("Cloud Computing","*Definitions1: Cloud computing is a computing-infrastructure and "
    			+ "software model for enabling ubiquitous access to shared pools of configurable resources\n"
    			+ "*Examples:PaaS, SaaS, IaaS\n"));
    	wordData.add(new Word("Distributed System","*Definitions1:  A distributed system is a model in which components located "
    			+ "on networked computers communicate and coordinate their actions to do one job by passing messages \n"
    			+ "*Examples:telecommunication networks, network applicaitons, real-time process control, parallel computation\n"));
    	wordData.add(new Word("fog computing","*Definitions1: A architecture that usues one or more collaborative endpoints to carry out"
    			+ "a substantial amount of processing, storage, communication, control, configuration... "
    			+ "\n*Examples:Fog computing add the intelligence to the end of the network.\n"));
    }
    
    //returns the word data as an observable list
    public ObservableList<Word> getWordData() {
    	return wordData;
    }


    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("xDictionary-Client");

        initRootLayout();

        showClientOverview();
    }

    /**
     * Initializes the root layout.
     */
    public void initRootLayout() {
        try {
            // Load Root layout overview.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("view/RootLayout.fxml"));
            rootLayout = (BorderPane) loader.load();

            // Set layout
            Scene scene = new Scene(rootLayout);
            primaryStage.setScene(scene);
            
            // give the controller access to the main app
            RootLayoutController controller = loader.getController();
            controller.setMainApp(this);
            
            primaryStage.show();
            
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        // try to load the last opened word list
        File file = this.getWordFilePath();
        if(file!=null) {
        	loadWordListFromFile(file);
        }

    }

    /**
     * Shows the Client overview inside the root layout.
     */
    public void showClientOverview() {
        try {
            // Load client overview.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("view/ClientOverView.fxml"));
            AnchorPane clientOverview = (AnchorPane) loader.load();

            // Set client overview into the center of root layout.
            rootLayout.setCenter(clientOverview);
            
            // give the controller access to the main app
            WordOverViewController controller = loader.getController();
            controller.setMainApp(this);
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Opens a dialog to edit details for the specified word. If the user
     * clicks OK, the changes are saved into the local word object and true
     * is returned.
     * 
     * @param local word object to be edited
     * @return true if the user clicked OK, false otherwise.
     */
    public boolean showWordEditDialog(Word word) {
    	try {
    		 // Load the fxml file and create a new stage for the popup dialog.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("view/WordEditDialog.fxml"));
            AnchorPane page = (AnchorPane) loader.load();
            
         // Create the dialog Stage.
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Edit Local Word");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);
            
            // Set the word into the controller.
            WordEditDialogController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setWord(word);
            
         // Show the dialog and wait until the user closes it
            dialogStage.showAndWait();
            
            return controller.isOKclicked();
    	}catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Returns the main stage.
     * @return
     */
    public Stage getPrimaryStage() {
        return primaryStage;
    }
    
    /**
     * Returns the local client word file preference, i.e. the file that was last opened.
     * The preference is read from the OS specific registry. If no such
     * preference can be found, null is returned.
     * 
     * @return
     */
    public File getWordFilePath() {
    	Preferences prefs = Preferences.userNodeForPackage(MainApp.class);
    	String filePath = prefs.get("filePath", null);
    	if(filePath != null) {
    		return new File(filePath);
    	}else {
    		return null;
    	}
    }
    /**
     * Sets the file path of the currently loaded file. The path is persisted in
     * the OS specific registry.
     * 
     * @param file the file or null to remove the path
     */
    public void setWordListFilePath(File file) {
        Preferences prefs = Preferences.userNodeForPackage(MainApp.class);
        if (file != null) {
            prefs.put("filePath", file.getPath());

            // Update the stage title.
            primaryStage.setTitle("xDictionary - " + file.getName());
        } else {
            prefs.remove("filePath");

            // Update the stage title.
            primaryStage.setTitle("xDictionary");
        }
    }
    public void saveWordListToFile(File file) {
        try {
        	JAXBContext context = JAXBContext
                    .newInstance(WordWrapper.class);
            Marshaller m = context.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            // Wrapping word class data.
            WordWrapper wrapper = new WordWrapper();
            wrapper.setWords(this.wordData);

            // Marshalling and saving XML to the file.
            m.marshal(wrapper, file);


            // Save the file path to the registry.
            setWordListFilePath(file);
        } catch (Exception e) { // catches ANY exception
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Could not save data");
            alert.setContentText("Could not save data to file:\n" + file.getPath());

            alert.showAndWait();
        }
    }
    public void loadWordListFromFile(File file) {
        try {
        	JAXBContext context = JAXBContext
                    .newInstance(WordWrapper.class);
            Unmarshaller um = context.createUnmarshaller();

            // Reading XML from the file and unmarshalling.
            WordWrapper wrapper = (WordWrapper) um.unmarshal(file);
            
            wordData.clear();
            wordData.addAll(wrapper.getWords());

            // Save the file path to the registry.
            setWordListFilePath(file);

        } catch (Exception e) { // catches ANY exception
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Could not load data");
            alert.setContentText("Could not load data from file:\n" + file.getPath());

            alert.showAndWait();
        }
    }
    
    

    public static void main(String[] args) {
    	
    	Client client = new Client();
		String sentence = null;
		BufferedReader userin = 
		new BufferedReader(
		new InputStreamReader(System.in));
		
		
		int length = args.length;
    	if(length >0 && length <=3) {
    		for(int n =0; n <length; n++) {
    			if(n == 0) {
    				ip = args[0];
    			}
    			if(n == 1) {
    				port = Integer.parseInt(args[1]);
    			}
    			if(n == 2) {
    				if(args[2].equalsIgnoreCase("command")) {
    					clientType = 1;
    				}
    			}
    		}					
		  	
    	// Command version client
		if(clientType == 1) {
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
			}
		}
		else {
			// GUI version client
			launch(args);
        }
    	}
    	else { 
    		// wrong jar input
    		System.out.println("invilate input");
    		System.exit(0);
    	}
    }

	public Client getClient() {
		return client;
	}

	public void setClient(Client client) {
		this.client = client;
	}

}
