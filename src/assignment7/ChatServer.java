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
import java.util.Observable;

public class ChatServer extends Observable{
	private ArrayList<PrintWriter> clientOutputStreams;
	public int roomSize = 0;

	public void setUpNetworking() throws Exception{
		clientOutputStreams = new ArrayList<PrintWriter>();
		@SuppressWarnings("resource")
		ServerSocket serverSock = new ServerSocket(4343);
		while(true){
		
				Socket clientSocket = serverSock.accept();
				ClientObserver writer = new ClientObserver(clientSocket.getOutputStream());
				Thread t = new Thread(new ClientHandler(clientSocket, roomSize++));
				t.start();
				this.addObserver(writer);
				System.out.println("Connection good");
			
		}
	}
	
	class ClientHandler implements Runnable{
		private BufferedReader reader;
		private int id;
		
		public ClientHandler(Socket clientSocket, int i) throws IOException{
			Socket sock = clientSocket;
			reader = new BufferedReader(new InputStreamReader(sock.getInputStream()));
			id = i;
		}
		
		public void run(){
			String message;
			try{
				while((message = reader.readLine())!=null){
					System.out.println("read"+ message);
					setChanged();
					notifyObservers("User"+id+": "+message);
				}
			}catch(IOException e){
				e.printStackTrace();
				
			}
		}
	}

}
