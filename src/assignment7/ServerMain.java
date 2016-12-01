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

import javafx.application.Application;

public class ServerMain {

	public static void main(String[] args){

		new Thread(() -> {
			Application.launch(ServerGUI.class, args);
		}).start();

		try{
			ChatServer server = new ChatServer();
			server.setUpNetworking();
		} catch(Exception e){
			e.printStackTrace();
		}
	}

}
