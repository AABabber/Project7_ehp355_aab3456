import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class ChatPrintWriterprivate ArrayList<PrintWriter> clientOutputStreams;
	
	private void setUpNetworking() throws Exception{
		clientOutputStreams = new ArrayList<PrintWriter>();
		
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

}
