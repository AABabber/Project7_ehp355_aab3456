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
import java.util.Observable;

public class ChatServer extends Observable{
	
	// private ArrayList<PrintWriter> clientOutputStreams;
	// public int roomSize = 0;
	private int port = 4343;	// TODO: Decide whether to have user defined port
	private BufferedReader nameReader;

	public void setUpNetworking() throws Exception{
		// clientOutputStreams = new ArrayList<PrintWriter>();
		@SuppressWarnings("resource")
		ServerSocket serverSock = new ServerSocket(port);
		while(true){
		
				Socket clientSocket = serverSock.accept();
				
				/* TODO: See if this method of getting the client's name is correct and fast enough
				 * 
				 * Note: readLine() should block
				 */
				nameReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream())); 
				String name = nameReader.readLine();
				
				ClientObserver writer = new ClientObserver(clientSocket.getOutputStream(), name);
				Thread t = new Thread(new ClientHandler(clientSocket));
				t.start();
				this.addObserver(writer);
				System.out.println("Connection good");
			
		}
	}
	
	class ClientHandler implements Runnable{
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
		
		public ClientHandler(Socket clientSocket) throws IOException{
			Socket sock = clientSocket;
			reader = new BufferedReader(new InputStreamReader(sock.getInputStream()));
			// id = i;
		}
		
		public void run(){
			String message;
			try{
				while((message = reader.readLine())!=null){
					// System.out.println("read"+ message);
					setChanged();
					notifyObservers(message);
				}
			}catch(IOException e){
				e.printStackTrace();
				
			}
		}
	}

}
