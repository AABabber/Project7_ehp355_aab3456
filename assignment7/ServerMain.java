import java.net.ServerSocket;

/* 
 * Aaron Babber
 * aab3456
 * 16480
 * Enrique Perez-Osborne
 * ehp355
 * 16465
 * Slip days used: <0>
 * Fall 2016
 */

public class ServerMain {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ServerSocket serverSock = new ServerSocket(9999);
		
		while(true){
			Socket clientSock = serverSock.accept();
			Thread t = new Thread(new ClientHandler(clientSocket));
			t.start();
			System.out.println("Connection made");			
		}
		
		
	}

}
