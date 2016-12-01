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

	// The name of the client we're implementing this Observer for
	private String name;
	@SuppressWarnings("unused")
	/* This variable functions as a convenient container
	 * when parsing the metadata.
	 */
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

		// DONE: Add user list updates
		String messageTag = ((String) arg).substring(0, 4);

		if (messageTag.equals("new:")) {
			this.println((String) arg);
			this.flush();
		}
		else if (messageTag.equals("req:")) {
			findRecipient(arg);
			send(arg);
		}
		else if (messageTag.equals("rep:")) {
			findOriginalSender(arg);
			send(arg);
		}
		else {
			findNames(arg);
			send(arg);
		}
	}

	private void send(Object arg) {
		for (String receiver : receivers) {
			if (receiver.equals(name)) {
				System.out.println((String) arg + " -- ClientObserver");	// TODO: Comment this when not testing
				this.println((String) arg);
				this.flush();
				break;
			}
		}
	}

	@SuppressWarnings("unused")
	// Parsing message of the form "req:sender [tab] to:recipient"
	private void findRecipient(Object arg) {
		String message = (String) arg;
		int senderEnd = message.indexOf('\t');
		String senderString = message.substring(0, senderEnd);

		// After this line, message will be equal to "to:recipient"
		message = ((String) arg).substring(senderEnd + 1, ((String) arg).length());
		int colonIndex = message.indexOf(':');
		String recipient = message.substring(colonIndex + 1, message.length());
		receivers = new String[1];
		receivers[0] = recipient;
	}

	@SuppressWarnings("unused")
	// Message form: "rep:recipient [tab] to:sender [tab] [reply]"
	private void findOriginalSender(Object arg) {
		String message = (String) arg;
		String cutMessage;
		int replyEnd = message.indexOf('\t');

		// replyString = "rep:recipient"
		String replyString = message.substring(0, replyEnd);
		int colonIndex = replyString.indexOf(':');
		String replier = replyString.substring(colonIndex + 1, replyString.length());

		// cutMessage = "to:sender [tab] [reply]"
		cutMessage = message.substring(replyEnd + 1, message.length());
		int senderEnd = cutMessage.indexOf('\t');
		// senderString = "to:sender"
		String senderString = cutMessage.substring(0, senderEnd);
		colonIndex = senderString.indexOf(':');
		String originalSender = senderString.substring(colonIndex + 1, senderString.length());

		receivers = new String[1];
		receivers[0] = originalSender;
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

		unusedSender = fromString.substring(5, fromString.length());

		// Strip the "to:" from the String of receivers
		receiverString = receiverString.substring(3, receiverString.length());

		// A String is a CharSequence - the parameter needed for contains()
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
