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
import java.io.*; 
import java.net.*; 
import javax.swing.*; 
import java.awt.*; 
import java.awt.event.*;

public class ClientMain {

	 
	
	public static void main(String[] args) {
		try {
			new ChatClient().run();
		} catch (Exception e) { e.printStackTrace(); }
	} 
	
	
}
