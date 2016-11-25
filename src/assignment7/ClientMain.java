package assignment7;

import java.util.ArrayList;

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

public class ClientMain {
	public static ArrayList<ChatClient> room = new ArrayList<ChatClient>();
	
	public static void main(String[] args) {
		try {
			new ChatClient().run();
		} catch (Exception e) { e.printStackTrace(); }
	} 
	
	
}
