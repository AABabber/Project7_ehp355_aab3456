package assignment7;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class ChatServer{
	private ArrayList<PrintWriter> clientOutputStreams;
	

	public void setUpNetworking() throws Exception{
		clientOutputStreams = new ArrayList<PrintWriter>();
		@SuppressWarnings("resource")
		ServerSocket serverSock = new ServerSocket(9000);
		while(true){
			Socket clientSocket = serverSock.accept();
			PrintWriter writer = new PrintWriter(clientSocket.getOutputStream());
			Thread t = new Thread(new ClientHandler(clientSocket));
			t.start();
			clientOutputStreams.add(writer);
			System.out.println("Connection good");
		}
	}
	
	private void notifyClients(String message){
		for(PrintWriter writer : clientOutputStreams){
			writer.println(message);
			writer.flush();
		}
	}
	
	class ClientHandler implements Runnable{
		private BufferedReader reader;
		
		public ClientHandler(Socket clientSocket) throws IOException{
			Socket sock = clientSocket;
			reader = new BufferedReader(new InputStreamReader(sock.getInputStream()));
			
		}
		
		public void run(){
			String message;
			try{
				while((message = reader.readLine())!=null){
					System.out.println("read"+ message);
					notifyClients(message);
				}
			}catch(IOException e){
				e.printStackTrace();
				
			}
		}
	}

}
