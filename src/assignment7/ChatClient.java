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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.stage.Stage;
import javafx.scene.Node;
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
	// TODO: Catch exceptions associated with clicking names in onlineList
	private String passwd;
	private ListView<String> onlineList;	
	private ListView<String> friendList;
	private TextArea clientConsole;
	// TODO: Replace with thread-safe version or only access from GUI thread
	private HashMap<String, Stage> currentChats;	
	private Stage MasterStage;
	
	@Override 
	public void start(Stage primaryStage) {
		onlineList = new ListView<String>();
		friendList = new ListView<String>();
		currentChats = new HashMap<String, Stage>();
		MasterStage = primaryStage;
		loginView(primaryStage);
		
	}
	
	
	// ---------------------------------------- PRIVATE METHODS ---------------------------------------- //
	
	//function to handle login GUI
	private void loginView(Stage primaryStage){
		GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        //grid.setPadding(new Insets(25, 25, 25, 25));
		
		Button close = new Button("Close");
		
		close.setFont(Font.font("System", 16));
		//close.setOnAction(new CloseLoginHandler());
		
		Button login = new Button("Login/Create Account");
		
		login.setFont(Font.font("System", 16));
		//login.setOnAction(new LoginHandler());
		
		Label loginLabel = new Label("Login:");
		Label passwdLabel = new Label("Password:");
		Label portLabel = new Label("Port:");
		Label hostLabel = new Label("Host:");
		
		TextField loginText = new TextField();
		TextField passwdText = new PasswordField();
		TextField portText = new TextField();
		TextField hostText = new TextField();
		
		
		// using grid and hbox
		grid.add(loginLabel, 0, 1);
		grid.add(loginText, 1, 1);
		grid.add(passwdLabel, 0, 2);
		grid.add(passwdText, 1, 2);
		grid.add(portLabel, 0, 3);
		grid.add(portText, 1, 3);
		grid.add(hostLabel, 0, 4);
		grid.add(hostText, 1, 4);
		
		HBox hbBtnLogin = new HBox(10);
        hbBtnLogin.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtnLogin.getChildren().add(login);
        grid.add(hbBtnLogin, 1, 5);
        
        HBox hbBtnClose = new HBox(10);
        hbBtnClose.setAlignment(Pos.BOTTOM_LEFT);
        hbBtnClose.getChildren().add(close);
        grid.add(hbBtnClose, 0, 5);
        
        close.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent e) {
                System.exit(0);
            }
        });
        
        login.setOnAction(new EventHandler<ActionEvent>(){
        	@Override
        	public void handle(ActionEvent e){
        		boolean init = true;
        		name = loginText.getText().toString();
        		passwd = passwdText.getText().toString();
        		port = Integer.parseInt(portText.getText().toString());
        		host = hostText.getText().toString();
        		System.out.println("Got login info -- ChatClient/loginView");	// TODO: Comment before submission
        		try {
        			init = setUpNetworking();
        		} catch (Exception exc) {
        			exc.printStackTrace();
        		}
        		System.out.println("Returned from setUpNetworking -- ChatClient/loginView");	// TODO: Comment before submission
        		System.out.println("About to call initView() -- ChatClient/loginView");	// TODO: Comment before submission
        		if(init==true){
        			initView(primaryStage);
        		}
        	}
        });
        
				
		// Create a scene and place it in the stage
		Scene scene = new Scene(grid, 450, 400);
		//primaryStage.setWidth(500);
		//primaryStage.setHeight(455);
		/* There are a lot of intricate components to this window, 
		 * so allowing resizing might inhibit the intended view. As such,
		 * we only allow one fixed size.
		 */
		primaryStage.setResizable(false);
		primaryStage.setScene(scene); // Place scene in stage
		
		primaryStage.show();
		
		
		
	}
	
	private void loginFailMessage(){
		Stage messageStage = new Stage();
		GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        Button close = new Button("Close");
        close.setFont(Font.font("System", 16));
        
        Label message = new Label("Existing User, Invalid Password");
        grid.add(message, 2, 2);
       
        HBox hbBtnClose = new HBox(10);
        hbBtnClose.setAlignment(Pos.BOTTOM_CENTER);
        hbBtnClose.getChildren().add(close);
        grid.add(hbBtnClose, 2, 4);
        
        close.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent e) {
                messageStage.close();
                loginView(MasterStage);
            }
        });
        
        // Create a scene and place it in the stage
     	Scene scene = new Scene(grid, 450, 400);
     	messageStage.setResizable(false);
     	messageStage.setScene(scene); // Place scene in stage
     	messageStage.show();
        
        
	}
	
	
	
	private void initView(Stage primaryStage) {
		
		// ---- Left portion of main window ---- //
		
		// Left - item 1
		/* This drop down menu consists of all the "extra features".
		 * We can call setOnAction for each MenuItem when we implement
		 * these features.
		 */
		MenuButton options = new MenuButton("Options");
		MenuItem history = new MenuItem("Chat History  ");
		history.setOnAction(new ChatHistoryHandler());
		MenuItem logout = new MenuItem("Logout");
		logout.setOnAction(new logoutHandler());
		MenuItem requests = new MenuItem("Friend Request  ");
		MenuItem pass = new MenuItem("Change Password  ");
		pass.setOnAction(new changePasswdHandler());
		options.getItems().addAll(history, requests, pass, logout);	// Add all options to the menu
		// Set the formatting of the drop down menu
		options.setLayoutX(30);
		options.setLayoutY(10);
		options.setPrefSize(140, 30);
		// TODO: Center everything in MenuButton
		options.setAlignment(Pos.BASELINE_LEFT);
		
		// Left - item 2
		/* This is a list of all users currently online. Whenever a new user
		 * logs in, the server updates this list for all clients.
		 * 
		 * TODO: Add a second tab for friends
		 */
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
		clientConsole.setWrapText(true);
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
		//Scanner in = new Scanner(System.in);
		//System.out.print("Enter a name: ");
		//name = in.nextLine();
		//in.close();
		//System.out.println("Name is: " + name);	
		
		primaryStage.setTitle("Client Console - " + name); // Stage title
		primaryStage.show(); // Display stage
		MasterStage = primaryStage;
		// TODO: Specify what happens when console Stage closes
		
		
	} 
	
	private boolean setUpNetworking() throws Exception {	
		
		// DONE: Create host and port private variables which are set by user
		
		// Create a new client socket
		Socket sock = new Socket(host, port);	
		// Get the client socket's input stream
		InputStreamReader streamReader = new InputStreamReader(sock.getInputStream());
		
		System.out.println("After setting up socket -- ChatClient/setUpNetworking");	// TODO: Comment before submission
		boolean loginFail = false;
		// Set the input and output stream private variables
		reader = new BufferedReader(streamReader); 
		writer = new PrintWriter(sock.getOutputStream());
		// Output the client name to the server so it can be uniquely identified
		writer.println(name);	
		/* Note: including this line is very important.
		 * If it were left out, the server would hang indefinitely.
		 */
		writer.flush();		
		writer.println(passwd);
		writer.flush();
	
		String next;
		
		System.out.println("before while loop -- ChatClient/setUpNetworking");	// TODO: Comment before submission
		
		while(!((next = reader.readLine()).equals("Done"))||!(next.equals("Failed"))){
			friendList.getItems().add(next);
			System.out.println("next value: " + next + " -- ChatClient/setUpNetworking");	// TODO: Comment before submission
			if(next.equals("Done")){
				break;
			}
			if(next.equals("Failed")){
				break;
			}
		}
		
		if(next.equals("Failed")){
			System.out.println("CAUGHT FAILED");
			try{
				reader.close();
				writer.close();
				sock.close();
			}catch(IOException e){
				
			}
			loginFailMessage();
			loginFail = true;
			return false;
		}
		
		System.out.println("friendList updated -- ChatClient/setUpNetworking");	// TODO: Comment before submission
		
		while(!((next = reader.readLine()).equals("Done"))){
			if(!onlineList.getItems().contains(next) && !next.equals(name)) {
				onlineList.getItems().add(next);
			}
			
			System.out.println("next value: " + next + " -- ChatClient/setUpNetworking");	// TODO: Comment before submission
			if(next.equals("Done")){
				break;
			}	
		}
				
		/* Create and start the "processing" thread. This thread will listen for
		 * incoming information from the server. This frees up the GUI (application)
		 * thread to update client windows and the like. If we tried to do everything
		 * in one thread, the application would likely freeze up for several seconds
		 * at a time after every action.
		 */
		
		Thread readerThread = new Thread(new IncomingReader());
		readerThread.start();
		return true;
		
	}
	
	
	// ----------------------------------------- INNER CLASSES ----------------------------------------- //
	
	class ChatHistoryHandler implements EventHandler<ActionEvent> {

		@Override
		public void handle(ActionEvent event) {
			clientConsole.appendText("Outputting chat history... " + "\n");
			writer.println("hist:"+name);
			writer.flush();
			
		}
		
		
	}
	
	class logoutHandler implements EventHandler<ActionEvent>{
		@Override
		public void handle(ActionEvent event){
			//TODO:
		}
	}
	
	class changePasswdHandler implements EventHandler<ActionEvent>{
		@Override
		public void handle(ActionEvent event){
			
		}
	}
	
	class ChatButtonHandler implements EventHandler<ActionEvent> {

		@Override
		public void handle(ActionEvent event) {
			
			// getSelectedItems() returns a read-only ObservableList of all selected items.
			ObservableList<String> outOfOrderSelected = onlineList.getSelectionModel().getSelectedItems();
			// DONE: Used to check for out of order names (ex. Bob, Carl versus Carl, Bob)
			SortedList<String> selected = outOfOrderSelected.sorted();
			
			if (selected.size() == 0) {
				return;
			}
			
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
		 	 */ 
			if (currentChats.containsKey(selectedNames)) {
				Stage current = currentChats.get(selectedNames);
				onlineList.getSelectionModel().clearSelection();
				current.show();
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
		
		public void populateChatWindow(Stage chatWindow, String selectedNames) {
			
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
			// TODO: Define what happens when chat window closes
			
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
			// System.out.println("Outgoing message:" + "\n" + outgoingMessage); 
			
			messageArea.setText("");
			chatContent.appendText(name + ": " + message + "\n");
			writer.println(outgoingMessage);
			writer.flush();
			
		}
		
	}
	
	class IncomingReader implements Runnable {
		
		private String messagePayload;
		private String sender;	
		private String[] receivers;
	
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
							// This updates the GUI from the application thread
							Platform.runLater(() -> onlineList.getItems().add(newUser));
						}
					}
					
					/* Message format:
					 * from:sender [tab] to:receiver1, receiver2, receiver3, ... [tab] [Actual message]
					 */
					else if (firstLetter.equals("f")) {	
						
						// Update sender and receivers
						findNames(message);
						
						// Construct appropriate key for client using fields sender, receivers, and name
						ArrayList<String> namesForKey = new ArrayList<String>(Arrays.asList(receivers));
						namesForKey.add(sender);
						Collections.sort(namesForKey);
						namesForKey.remove(name);
						
						String key = "";
						for (int i = 0; i < namesForKey.size(); i++) {
							if (i == (namesForKey.size() - 1)) {
								key = key + namesForKey.get(i);
							}
							else {
								key = key + namesForKey.get(i) + ", ";
							}
						}
						
						/* We check if there's an existing chat for the received message. 
						 * If so, use the key to grab the appropriate Stage, and put
						 * the message in the TextArea (from the application thread). 
						 * 
						 * If not, construct a new chat window (also from the application thread) 
						 * using the code from the end of ChatButtonHandler's handle method. 
						 * After that, put the message in the TextArea. 
						 * 
						 * Make sure to prepend the message with the user's name.
						 */
						if (currentChats.containsKey(key)) {
							updateChatWindow(key);
						}
						else {
							createNewChatWindow(key);
						}
			
					}
					
					else if(firstLetter.equals("h")){
						String histItem = message.substring(5, message.length());
						Platform.runLater(() -> {
							clientConsole.appendText(histItem + "\n");
						});
						
					}
					
				}
			} catch (IOException ex) { 
				ex.printStackTrace(); 
			}
		}
		
		private void updateChatWindow(String key) {
			Platform.runLater(() -> {
				Stage currentWindow = currentChats.get(key);
				VBox chatLayout = (VBox) currentWindow.getScene().getRoot();
				TextArea chatDisplay = (TextArea) chatLayout.getChildren().get(0);
				chatDisplay.appendText(sender + ": " + messagePayload + "\n");
				currentWindow.show();
			});
		}
		
		private void createNewChatWindow(String key) {
			Platform.runLater(() -> {
				Stage newChatWindow = new Stage();
				ChatButtonHandler helperClass = new ChatButtonHandler();
				helperClass.populateChatWindow(newChatWindow, key);
				currentChats.put(key, newChatWindow);
				VBox chatLayout = (VBox) newChatWindow.getScene().getRoot();
				TextArea chatDisplay = (TextArea) chatLayout.getChildren().get(0);
				chatDisplay.appendText(sender + ": " + messagePayload + "\n");
				newChatWindow.show();
			});
		}
		
		private void findNames(String arg) {
			// It's not safe to use a regex as the user message might have tabs and whatnot
			String message = arg;
			
			/* The first parameter in substring() is the beginning index, inclusive, and the 
			 * second parameter is the ending index, exclusive.
			 */
			int fromEnd = message.indexOf('\t');
			String fromString = message.substring(0, fromEnd);
			
			// Update message as a sender could have the string "to" in their name
			message = arg.substring(fromEnd + 1, arg.length());
			int receiveEnd = message.indexOf('\t');
			String receiverString = message.substring(0, receiveEnd);
			
			messagePayload = message.substring(receiveEnd + 1, message.length());
			sender = fromString.substring(5, fromString.length());
			
			// Strip the "to:" from the String of receivers
			receiverString = receiverString.substring(3, receiverString.length());
			
			// A String is a CharSequence - the parameter needed for contains()
			String comma = ",";
			if (!receiverString.contains(comma)) {
				receivers = new String[1];
				receivers[0] = receiverString;
			}
			else {
				receivers = receiverString.split(", ");
			}
		}
		
	}
	
}
