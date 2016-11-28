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
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Observable;

public class ChatServer extends Observable {
	
	// A list of the users currently "online"
	private ArrayList<String> activeUsers;	
	private int port = 4343;	// TODO: Decide whether to have user defined port
	private BufferedReader nameReader;	// An input stream reader to determine a client's name

	public void setUpNetworking() throws Exception {
		activeUsers = new ArrayList<String>();
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
				nameReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream())); 
				// readLine() will wait until there's something in the input stream (i.e. it "blocks")
				String name = nameReader.readLine();	
				// Print to console on successful connection
				System.out.println("Connection good for: " + name);	// TODO: Delete?
				
				// The output stream portion of a client socket is effectively the Observer
				ClientObserver writer = new ClientObserver(clientSocket.getOutputStream(), name);
				// TODO: Determine if order of this call matters
				this.addObserver(writer); 
				updateUsers(name);	// Update online user lists of all clients
				
				/* This creates a new thread which constantly listens for input
				 * from the client connection which was just established.
				 */
				Thread t = new Thread(new ClientHandler(clientSocket));
				t.start();
				
		}
	}
	
	
	// ---------------------------------------- PRIVATE METHODS ---------------------------------------- //
	
	
	private void updateUsers(String name) {
		activeUsers.add(name);
		for (String userName : activeUsers) {
			String friendUpdate = "new:" + userName;
			setChanged();
			notifyObservers(friendUpdate);
		}
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
		
		public ClientHandler(Socket clientSocket) throws IOException {
			Socket sock = clientSocket;
			reader = new BufferedReader(new InputStreamReader(sock.getInputStream()));
		}
		
		public void run() {
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
					}
					
				}
			}catch(IOException e){
				e.printStackTrace();
			}
			/* If the thread finishes because of a GUI closing, does 
			 * control come here?
			 */
		}
		
	}

}
