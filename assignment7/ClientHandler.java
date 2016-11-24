import java.io.BufferedReader;
import java.io.PrintWriter;

public class ClientHandler implements Runnable {
	private BufferedReader reader;
	
	public ClientHandler(Socket clientSocket) throws IOException{
		Socket cSock = clientSocket;
		reader = new BufferedReader(new InputStreamReader(sock.getInputStream()));
	}
	
	public void run(){
		String message;
		while((message = reader.readLine())!= null){
			notifyClients(message);
		}
	}
	
	private void notifyClients(String message){
		for(PrintWriter writer : clientOutputStreams){
			writer.println(message);
			writer.flush();
		}
	}
}
