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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Observable;
import java.util.concurrent.ConcurrentHashMap;

public class ChatServer extends Observable {
	
	// A list of the users currently "online"
	private ArrayList<String> activeUsers;	
	private ConcurrentHashMap<String, ArrayList<String>> chatHistory;
	private ConcurrentHashMap<String, ArrayList<String>> friendList;
	private ConcurrentHashMap<String, String> userAndPasswd;
	private int port = 4343;	// TODO: Decide whether to have user defined port
	private BufferedReader loginReader;	// An input stream reader to determine a client's name

	public void setUpNetworking() throws Exception {
		boolean userExists = false;
		boolean userMatchesPasswd =false;
		activeUsers = new ArrayList<String>();
		chatHistory = new ConcurrentHashMap<String, ArrayList<String>>();	// TODO: Double check this initialization
		userAndPasswd = new ConcurrentHashMap<String, String>();
		friendList = new ConcurrentHashMap<String,ArrayList<String>>();
		@SuppressWarnings("resource")
		ServerSocket serverSock = new ServerSocket(port);	// Set up the server socket
		// Serve clients indefinitely
		while(true){
			
				/* The accept() method will make the server wait until there's 
				 * an incoming request from a client socket. After it receives such a request,
				 * it executes the code which follows.
				 */
				Socket clientSocket = serverSock.accept();
				// Used to read a client's name
				loginReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream())); 
				
				// readLine() will wait until there's something in the input stream (i.e. it "blocks")
				String name = loginReader.readLine();
				String pass = loginReader.readLine();
				
				//TODO:implement check of name
				if(userAndPasswd.containsKey(name)){
					userExists = true;
					if(userAndPasswd.get(name).equals(pass)){
						activeUsers.add(name);
						userMatchesPasswd =true;
					}else{
						userMatchesPasswd = false;
					}
				}else{
					userExists =false;
				}
				
				// Print to console on successful connection
				System.out.println("Connection good for: " + name);	// TODO: Delete?
				
				// The output stream portion of a client socket is effectively the Observer
				ClientObserver writer = new ClientObserver(clientSocket.getOutputStream(), name);
				// TODO: Determine if order of this call matters
				this.addObserver(writer); 
				if(userExists==false){
					activeUsers.add(name);
					
					//updateUsers(name);	// Update online user lists of all clients
					addUsers(name, pass);
				}
				/* This creates a new thread which constantly listens for input
				 * from the client connection which was just established.
				 */
				Thread t = new Thread(new ClientHandler(clientSocket, userExists, userMatchesPasswd, name));
				t.start();
				
		}
	}
	
	
	// ---------------------------------------- PRIVATE METHODS ---------------------------------------- //
	
	
	private void updateUsers(String name) {
		// activeUsers.add(name);
		for (String userName : activeUsers) {
			String friendUpdate = "new:" + userName;
			setChanged();
			notifyObservers(friendUpdate);
		}
	}
	
	private void addUsers(String name, String pass){
		userAndPasswd.put(name, pass);
		//TODO: does anything else need to be done to this HashMap ?
	}
	
	
	// ----------------------------------------- INNER CLASSES ----------------------------------------- //
	
	
	/* We're going to use message metadata to identify senders and receivers.
	 * 
	 * The message String will be of the form:
	 * 
	 * from:sender [tab] to:receiver1, receiver2, receiver3, ... [tab] [Actual message]
	 * 
	 * It doesn't matter if the user types from:sender or to:receiver, because we prepend 
	 * it to the message and only process the first occurrence of each.
	 * 
	 * There are several other tags we use for other client actions. This seemed like the 
	 * most efficient way to communicate between client and server as Strings can be passed
	 * around very easily as opposed to Java collections objects.
	 */
	class ClientHandler implements Runnable {
		
		private BufferedReader reader;
		private PrintWriter historyWriter;
		private boolean userExists;
		private boolean userMatchesPasswd;
		private String name;
		private Socket sock;
		
		public ClientHandler(Socket clientSocket, boolean uE, boolean uMP, String n) throws IOException {
			sock = clientSocket;
			reader = new BufferedReader(new InputStreamReader(sock.getInputStream()));
			historyWriter = new PrintWriter(sock.getOutputStream());
			userExists = uE;
			userMatchesPasswd = uMP;
			this.name=n;
		}
		
		public void run() {
			if(userExists==false){
			
				historyWriter.println("Done");
				historyWriter.flush();
				for(String s: activeUsers){
					historyWriter.println(s);
					historyWriter.flush();
				}
				historyWriter.println("Done");
				historyWriter.flush();
				updateUsers(name);
				
			}else if(userExists==true && userMatchesPasswd==true){
				ArrayList<String> friends = friendList.get(name);
				
				if (friends != null) {
					for(String s: friends){
						historyWriter.println(s);
						historyWriter.flush();
					}
				}
				historyWriter.println("Done");
				historyWriter.flush();
				
				for(String s: activeUsers){
					historyWriter.println(s);
					historyWriter.flush();
				}
				historyWriter.println("Done");
				historyWriter.flush();
				updateUsers(name);
				
			}else if(userExists==true && userMatchesPasswd==false){
				
				historyWriter.println("Failed");
				historyWriter.flush();
				try {
					historyWriter.close();
					reader.close();
					sock.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				//would not allow me to place a continue outside of a loop
				//continue;
				
				
			}
			
			
			
			String message;		// The input from the client
			
			try{
				while((message = reader.readLine()) != null){
					
					// Determines what kind of action the client is performing
					String firstLetter = Character.toString(message.charAt(0));
					
					// The client is sending a message
					if (firstLetter.equals("f")) {
						setChanged();
						// Calls update() for each Observer
						notifyObservers(message);
						updateHistory(message);
					}else if(firstLetter.equals("h")){
						String userName = message.substring(5, message.length());
						if(!chatHistory.containsKey(userName)) {
							continue;
						}
						ArrayList<String> histToSend = chatHistory.get(userName);
						// TODO: watch for possible lage do to for loop, if so, 
						// threading could be possible solution
						for(String s : histToSend){
							historyWriter.println("hist:"+s);
							historyWriter.flush();
						}
						
					}else if(firstLetter.equals("c")){
						
						//NEED to check if parsing string correctly
						String userName = message.substring(3,message.indexOf("\t"));
						String newPasswd = message.substring(message.indexOf("\t")+1,message.length());
						System.out.println(newPasswd);
						userAndPasswd.put(userName,newPasswd);
						
					}
					
				}
			}catch(IOException e){
				e.printStackTrace();
			}
			/* If the thread finishes because of a GUI closing, does 
			 * control come here?
			 */
		}
		
		private void updateHistory(String message) {
			ArrayList<String> userHist = findName(message);
			for(String user : userHist) {
				if(chatHistory.containsKey(user)){
					ArrayList<String> temp = chatHistory.get(user);
					temp.add(message);
				}else{
					ArrayList<String> newHist = new ArrayList<String>();
					chatHistory.put(user, newHist);
					newHist.add(message);
				}
			}
		}
		
		private ArrayList<String> findName(String arg){
			// It's not safe to use a regex as the user message might have tabs and whatnot
			// TODO: Prevent user from having tabs, commas, or spaces in their username
			String message = arg;
			ArrayList<String> users = new ArrayList<String>();
			
			/* The first parameter is the beginning index, inclusive, and the 
			 * second parameter is the ending index, exclusive.
			 */
			int fromEnd = message.indexOf('\t');
			String fromString = message.substring(0, fromEnd);
			
			// Update message as a sender could have the string "to" in their name
			message = arg.substring(fromEnd + 1, arg.length());
			int receiveEnd = message.indexOf('\t');
			String receiverString = message.substring(0, receiveEnd);
			
			users.add(fromString.substring(5, fromString.length()));
			
			// Strip the "to:" from the String of receivers
			receiverString = receiverString.substring(3, receiverString.length());
			
			// A String is a CharSequence - the parameter needed for contains()
			String comma = ",";
			if (!receiverString.contains(comma)) {
				users.add(receiverString);
			}
			else {
				String[] receivers = receiverString.split(", ");
				for (String r : receivers) {
					users.add(r);
				}
			}
			
			Collections.sort(users);
			return users;
		}
		
	}

}
