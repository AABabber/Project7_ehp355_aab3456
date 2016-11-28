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
import java.util.HashMap;
import java.util.Scanner;

import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.layout.*;
import javafx.scene.text.Font;

public class ChatClient extends Application {
	
	// TODO: Set host, port, and name using a login screen
	private int port = 4343;
	private String host = "127.0.0.1";
	private BufferedReader reader;
	private PrintWriter writer;
	private String name;	// TODO: Don't allow there to exist duplicate usernames
	private ListView<String> onlineList;
	private TextArea clientConsole;
	private HashMap<String, Stage> currentChats;	
	
	@Override 
	public void start(Stage primaryStage) {
		currentChats = new HashMap<String, Stage>();
		initView(primaryStage);
		try {
			setUpNetworking();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	// ---------------------------------------- PRIVATE METHODS ---------------------------------------- //
	
	
	private void initView(Stage primaryStage) {
		
		// ---- Left portion of main window ---- //
		
		// Left - item 1
		/* This drop down menu consists of all the "extra features".
		 * We can call setOnAction for each MenuItem when we implement
		 * these features.
		 */
		MenuButton options = new MenuButton("Options");
		MenuItem history = new MenuItem("Chat History  ");
		MenuItem requests = new MenuItem("Friend Request  ");
		MenuItem pass = new MenuItem("Change Password  ");
		options.getItems().addAll(history, requests, pass);	// Add all options to the menu
		// Set the formatting of the drop down menu
		options.setLayoutX(30);
		options.setLayoutY(10);
		options.setPrefSize(140, 30);
		// TODO: Center everything in MenuButton
		options.setAlignment(Pos.BASELINE_LEFT);
		
		// Left - item 2
		/* This is a list of all users currently online. Whenever a new user
		 * logs in, the server updates this list for all clients.
		 */
		onlineList = new ListView<String>();
		// Allow multiple selections in ListView
		onlineList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);	
		ScrollPane onlineView = new ScrollPane(onlineList);	// Wrap ListView inside a ScrollPane
		// Set the formatting of the ScrollPane showing the online users
		onlineView.setLayoutX(0);
		onlineView.setLayoutY(50);
		onlineView.setPrefSize(200, 332);
		onlineView.setHbarPolicy(ScrollBarPolicy.NEVER);	// Don't show a horizontal scoll bar
		onlineView.setVbarPolicy(ScrollBarPolicy.AS_NEEDED);
		onlineList.prefWidthProperty().bind(onlineView.widthProperty());
		onlineList.setPrefHeight(332);

		// Left - item 3
		/* After the client has selected which users they would like to chat with
		 * (of those online), pressing this button will open the window for 
		 * the session.
		 */
		Button startChat = new Button("Chat");
		// Formatting the button
		startChat.setLayoutX(15);
		startChat.setLayoutY(387);
		startChat.setPrefSize(170, 40);
		// The Font.font() method enables you to specify the font family name and size.
		startChat.setFont(Font.font("System", 16));
		/* DONE: Call setOnAction - remember to clear onlineList selection
		 * 
		 * This call specifies what happens when the button is pressed.
		 * The ChatButtonHandler will start a new chat if appropriate, and update
		 * the necessary internal fields as well. The ChatButtonHandler is implemented
		 * as an inner class below so it has access to the appropriate instance variables.
		 */
		startChat.setOnAction(new ChatButtonHandler());	
		
		// Left container
		AnchorPane sideBar = new AnchorPane();
		sideBar.getChildren().addAll(options, onlineView, startChat);
		
		// ---- Right (technically "Center") portion of main window ---- //
		
		/* This TextArea will display status information and error messages
		 * associated with client actions. For example, if friend lists
		 * are implemented, this is where we'd output an error message if 
		 * this client attempts to chat with someone who isn't their friend.
		 */
		clientConsole = new TextArea();
		clientConsole.setEditable(false);
		// Container for the console which allows scrolling
		ScrollPane consolePane = new ScrollPane(clientConsole);	
		// Formatting the console area
		consolePane.setHbarPolicy(ScrollBarPolicy.NEVER);
		consolePane.setVbarPolicy(ScrollBarPolicy.AS_NEEDED);
		clientConsole.prefWidthProperty().bind(consolePane.widthProperty());
		clientConsole.setPrefHeight(433);
		
		// The high-level layout holding everything above
		BorderPane mainPane = new BorderPane();
		mainPane.setLeft(sideBar);
		mainPane.setCenter(consolePane);
		
		// Create a scene and place it in the stage
		Scene scene = new Scene(mainPane, 500, 455);
		primaryStage.setWidth(500);
		primaryStage.setHeight(455);
		/* There are a lot of intricate components to this window, 
		 * so allowing resizing might inhibit the intended view. As such,
		 * we only allow one fixed size.
		 */
		primaryStage.setResizable(false);
		primaryStage.setScene(scene); // Place scene in stage
		
		// DONE: Temporarily set name here; delete code when login screen is implemented
		Scanner in = new Scanner(System.in);
		System.out.print("Enter a name: ");
		name = in.nextLine();
		in.close();
		System.out.println("Name is: " + name);
		
		primaryStage.setTitle("Client Console - " + name); // Stage title
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
		
		// DONE: Create host and port private variables which are set by user
		
		/* BufferedReader and PrintWriter are better than DataInputStream and  
		 * DataOutputStream for String processing
		 */
		@SuppressWarnings("resource") 
		// Create a new client socket
		Socket sock = new Socket(host, port);	
		// Get the client socket's input stream
		InputStreamReader streamReader = new InputStreamReader(sock.getInputStream());
		// Set the input and output stream private variables
		reader = new BufferedReader(streamReader); 
		writer = new PrintWriter(sock.getOutputStream());
		// Output the client name to the server so it can be uniquely identified
		writer.println(name);	
		/* Note: including this line is very important.
		 * If it were left out, the server would hang indefinitely.
		 */
		writer.flush();		
		
		/* Create and start the "processing" thread. This thread will listen for
		 * incoming information from the server. This frees up the GUI (application)
		 * thread to update client windows and the like. If we tried to do everything
		 * in one thread, the application would likely freeze up for several seconds
		 * at a time after every action.
		 */
		Thread readerThread = new Thread(new IncomingReader());
		readerThread.start();
	}
	
	
	// ----------------------------------------- INNER CLASSES ----------------------------------------- //
	
		
	class ChatButtonHandler implements EventHandler<ActionEvent> {

		@Override
		public void handle(ActionEvent event) {
			
			// Get selected names
			ObservableList<String> selected = onlineList.getSelectionModel().getSelectedItems();
			
			/* We don't need to check if the selected names are online 
			 * if we properly update onlineList upon client "leaving"
			 */ 			
			String selectedNames = "";
			for (int i = 0; i < selected.size(); i++) {
				if (i == (selected.size() - 1)) {
					selectedNames = selectedNames + selected.get(i);
				}
				else {
					selectedNames = selectedNames + selected.get(i) + ", ";
				}
			}
			
			// Later: Do additional check for friend list
		
			/* This is a check to see if there's already a chat with the selected names.
		 	 * If so, we switch focus to the existing chat. If there isn't an existing
		 	 * chat, we create a new key-value pair in currentChats and populate the
		 	 * window.
		 	 * 
		 	 * TODO: Check for out of order names (ex. Bob, Carl versus Carl, Bob)
		 	 */ 
			if (currentChats.containsKey(selectedNames)) {
				Stage current = currentChats.get(selectedNames);
				current.requestFocus();		
			}
			else {
				Stage newChatWindow = new Stage();
				populateChatWindow(newChatWindow, selectedNames);
				currentChats.put(selectedNames, newChatWindow);
				onlineList.getSelectionModel().clearSelection();
				newChatWindow.show();
			}
		
		}
		
		private void populateChatWindow(Stage chatWindow, String selectedNames) {
			
			// TODO: Bind properties and wrap text for both of these areas 
			TextArea chatContent = new TextArea();
			chatContent.setEditable(false);
			chatContent.setPrefSize(375, 335);
			chatContent.setWrapText(true);
			TextField messageArea = new TextField();
			messageArea.setPromptText("Message");
			messageArea.setPrefSize(375, 70);
			// DONE: Define action for messageArea
			messageArea.setOnAction(new TextFieldHandler(selectedNames, chatContent, messageArea));
			
			VBox layout = new VBox();
			layout.getChildren().add(chatContent);
			layout.getChildren().add(messageArea);
			layout.setPrefSize(375, 400);
			
			Scene scene = new Scene(layout, 375, 400);
			chatWindow.setWidth(375);
			chatWindow.setHeight(400);
			chatWindow.setScene(scene);
			chatWindow.setTitle(name + " -- " + selectedNames);
			
			/*
			VBox test = (VBox) chatWindow.getScene().getRoot();
			test.getChildren();
			*/
		}
		
	}
	
	class TextFieldHandler implements EventHandler<ActionEvent> {
		
		private String selectedNames;
		private TextArea chatContent;
		private TextField messageArea;
		
		public TextFieldHandler(String selectedNames, TextArea chatContent, TextField messageArea) {
			this.selectedNames = selectedNames;
			this.chatContent = chatContent;
			this.messageArea = messageArea;
		}

		@Override
		/* Message format:
		 * from:sender [tab] to:receiver1, receiver2, receiver3, ... [tab] [Actual message]
		 */
		public void handle(ActionEvent event) {
			
			// TODO: Store this for chat history
			String message = messageArea.getText();
			String outgoingMessage = "from:" + name + "\t" + "to:" + selectedNames 
									 + "\t" + message;
			
			// TODO: Comment this when not testing
			System.out.println("Outgoing message:" + "\n" + outgoingMessage); 
			
			messageArea.setText("");
			chatContent.appendText(name + ": " + message + "\n");
			writer.println(outgoingMessage);
			writer.flush();
			
		}
		
	}
	
	class IncomingReader implements Runnable {
	
		public void run() {
			String message;	  // The input from the server
			try {
			
				// TODO: Determine whether this condition is effectively endless
				
				while ((message = reader.readLine()) != null) {
					
					/* Because the start of any message is metadata that we sent from the
					 * client side, the first letter will determine what kind of information
					 * we're receiving from the server.
					 */
					String firstLetter = Character.toString(message.charAt(0));
					
					// Using tag "new:" for onlineList updates
					if (firstLetter.equals("n")) {	
						String newUser = message.substring(4, message.length());
						// DONE: This dynamically updates the ListView
						if(!onlineList.getItems().contains(newUser) && !newUser.equals(name)) {
							onlineList.getItems().add(newUser);
						}
					}
					
					// First tag for messages is "from:"
					else if (firstLetter.equals("f")) {	
						// TODO: Process message
						// Check if there's a chat between this client and the sender
					}
					
				}
			} catch (IOException ex) { 
				ex.printStackTrace(); 
			}
		}
		
	}
	
}
