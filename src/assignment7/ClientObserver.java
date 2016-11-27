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

import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Observable;
import java.util.Observer;

public class ClientObserver extends PrintWriter implements Observer {
	
	private String name;
	//private String messagePayload;
	@SuppressWarnings("unused")
	private String unusedSender;
	private String[] receivers;
	
	public ClientObserver(OutputStream out, String name) {
		super(out);
		this.name = name;
	}
	
	@Override
	/* Message format:
	 * from:sender [tab] to:receiver1, receiver2, receiver3, ... [tab] [Actual message]
	 */
	public void update(Observable o, Object arg) {
		
		// TODO: Add user list updates
		String messageTag = ((String) arg).substring(0, 4);
		
		if (messageTag.equals("new:")) {
			this.println((String) arg);
			this.flush();
		}
		else {
			findNames(arg);
			send(arg);
		}
	}
	
	private void send(Object arg) {
		for (String receiver : receivers) {
			if (receiver.equals(name)) {
				// System.out.println((String) arg);	// TODO: Comment this when not testing
				this.println((String) arg);
				this.flush();
				break;
			}
		}
	}
	
	private void findNames(Object arg) {
		// It's not safe to use a regex as the user message might have tabs and whatnot
		// TODO: Prevent user from having tabs, commas, or spaces in their username
		String message = (String) arg;
		
		/* The first parameter is the beginning index, inclusive, and the 
		 * second parameter is the ending index, exclusive.
		 */
		int fromEnd = message.indexOf('\t');
		String fromString = message.substring(0, fromEnd);
		
		// Update message as a sender could have the string "to" in their name
		message = ((String) arg).substring(fromEnd + 1, ((String) arg).length());
		int receiveEnd = message.indexOf('\t');
		String receiverString = message.substring(0, receiveEnd);
		
		//messagePayload = ((String) arg).substring(receiveEnd + 1, ((String) arg).length());
		unusedSender = fromString.substring(5, fromString.length());
		
		// Strip the "to:" from the String of receivers
		receiverString = receiverString.substring(3, receiverString.length());
		
		// A String is a CharSequence
		String comma = ",";
		if (!receiverString.contains(comma)) {
			receivers = new String[1];
			receivers[0] = receiverString;
		}
		else {
			receivers = receiverString.split(", ");
		}
	}
}