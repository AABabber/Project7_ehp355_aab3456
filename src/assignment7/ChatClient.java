/* EE422C Project 7
 * Aaron Babber
 * aab3456
 * 16480
 * Enrique Perez-Osborne
 * ehp355
 * 16465
 * Slip days used: <0>
 * Fall 2016
 */

package assignment7;

import java.io.*; 
import java.net.*;
// import java.util.Observable;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.layout.*;
import javafx.scene.text.Font;

public class ChatClient extends Application {
	
	/* TODO: Create equivalent versions of all of these fields
	 * 
	 * reader and writer can still be used
	 */
	
	/*
	private JTextArea incoming; 
	private JTextField outgoing;
	private BufferedReader reader;
	private PrintWriter writer;
	public String header;
	*/
	
	@Override // Override the start method in the Application class
	public void start(Stage primaryStage) {
		initView(primaryStage);
		try {
			setUpNetworking();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void initView(Stage primaryStage) {
		
		// ---- Left portion of main window ---- //
		
		// Left - item 1
		MenuButton options = new MenuButton("Options");
		MenuItem history = new MenuItem("Chat History  ");
		MenuItem requests = new MenuItem("Friend Request  ");
		MenuItem pass = new MenuItem("Change Password  ");
		options.getItems().addAll(history, requests, pass);
		options.setLayoutX(30);
		options.setLayoutY(10);
		options.setPrefSize(140, 30);
		// TODO: Center everything in MenuButton
		options.setAlignment(Pos.BASELINE_LEFT);
		
		// Left - item 2
		// TODO: Populate friendList
		ListView<String> friendList = new ListView<String>();
		ScrollPane friendView = new ScrollPane(friendList);
		friendView.setLayoutX(0);
		friendView.setLayoutY(50);
		friendView.setPrefSize(200, 332);
		friendView.setHbarPolicy(ScrollBarPolicy.NEVER);
		
		// Left - item 3
		Button startChat = new Button("Chat");
		startChat.setLayoutX(15);
		startChat.setLayoutY(387);
		startChat.setPrefSize(170, 40);
		// The Font.font() method enables you to specify the font family name and size.
		startChat.setFont(Font.font("System", 16));
		
		// Left container
		AnchorPane sideBar = new AnchorPane();
		sideBar.getChildren().addAll(options, friendView, startChat);
		
		// ---- Right (technically "Center") portion of main window ---- //
		// TODO: Wrap inside a ScrollPane
		TextArea clientConsole = new TextArea();
		clientConsole.setEditable(false);
		
		BorderPane mainPane = new BorderPane();
		mainPane.setLeft(sideBar);
		mainPane.setCenter(clientConsole);
		
		// Create a scene and place it in the stage
		Scene scene = new Scene(mainPane, 500, 455);
		primaryStage.setWidth(500);
		primaryStage.setHeight(455);
		primaryStage.setResizable(false);
		primaryStage.setTitle("Client Console"); // Stage title
		primaryStage.setScene(scene); // Place scene in stage
		primaryStage.show(); // Display stage
		
		// TODO: Include incoming field in JavaFX version
		/*
		incoming = new JTextArea(15, 50);	
		incoming.setLineWrap(true); 
		incoming.setWrapStyleWord(true); 
		incoming.setEditable(false);
		*/
	
		// TODO: Include outgoing field in JavaFX version
		/*
		outgoing = new JTextField(20); 
		JButton sendButton = new JButton("Send"); 
		sendButton.addActionListener(new SendButtonListener());
		*/

	} 
	
	private void setUpNetworking() throws Exception {
		
		// TODO: Create host and port private variables which are set by user
		
		/* BufferedReader and PrintWriter are better than DataInputStream and  
		 * DataOutputStream for String processing
		 */
		
		/*
		@SuppressWarnings("resource") 
		Socket sock = new Socket("127.0.0.1", 4343);
		InputStreamReader streamReader = new InputStreamReader(sock.getInputStream());
		
		reader = new BufferedReader(streamReader); 
		writer = new PrintWriter(sock.getOutputStream());
		System.out.println("networking established");  
		*/
		
		// TODO: Decide whether to use lambda expression or inner class for thread
		/*
		Thread readerThread = new Thread(new IncomingReader());
		readerThread.start();
		*/
	}
	
	/*
	class SendButtonListener implements ActionListener {
		
		public void actionPerformed(ActionEvent ev) {
			
			writer.println(outgoing.getText()); 
			writer.flush();
			outgoing.setText(""); 
			outgoing.requestFocus();	
		}
	
	}
	*/
	
	/*
	class IncomingReader implements Runnable {
	
		public void run() {
			String message; 
			try {
			
				// TODO: Determine whether this condition is effectively endless
				 * 
				while ((message = reader.readLine()) != null) {
					incoming.append(message + "\n");
				}
			} catch (IOException ex) { ex.printStackTrace(); }
		}
		
	}
	*/
}
