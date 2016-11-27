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
	
	private ArrayList<String> activeUsers;
	private int port = 4343;	// TODO: Decide whether to have user defined port
	private BufferedReader nameReader;

	public void setUpNetworking() throws Exception {
		activeUsers = new ArrayList<String>();
		@SuppressWarnings("resource")
		ServerSocket serverSock = new ServerSocket(port);
		while(true){
		
				Socket clientSocket = serverSock.accept();
				nameReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream())); 
				String name = nameReader.readLine();	
				System.out.println("Connection good for: " + name); 

				ClientObserver writer = new ClientObserver(clientSocket.getOutputStream(), name);
				// TODO: Determine if order of this call matters
				this.addObserver(writer); 
				updateUsers(name);
				
				Thread t = new Thread(new ClientHandler(clientSocket));
				t.start();
				
		}
	}
	
	private void updateUsers(String name) {
		activeUsers.add(name);
		for (String userName : activeUsers) {
			String friendUpdate = "new:" + userName;
			setChanged();
			notifyObservers(friendUpdate);
		}
	}
	
	class ClientHandler implements Runnable {
		private BufferedReader reader;
		// private int id;
		
		/* We're going to use message metadata to identify senders and receivers.
		 * 
		 * The message String will be of the form:
		 * 
		 * from:sender [tab] to:receiver1, receiver2, receiver3, ... [tab] [Actual message]
		 * 
		 * It doesn't matter if the user types from:sender or to:receiver, because we prepend 
		 * it to the message and only process the first occurrence of each.
		 */
		
		public ClientHandler(Socket clientSocket) throws IOException {
			Socket sock = clientSocket;
			reader = new BufferedReader(new InputStreamReader(sock.getInputStream()));
			// id = i;
		}
		
		public void run() {
			String message;
			try{
				while((message = reader.readLine()) != null){
					
					String firstLetter = Character.toString(message.charAt(0));
					
					if (firstLetter.equals("f")) {
						setChanged();
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
