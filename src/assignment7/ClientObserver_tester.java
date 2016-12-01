package assignment7;

public class ClientObserver_tester {

	@SuppressWarnings("resource")
	public static void main(String[] args) {
		/* Message format:
		 * from:sender [tab] to:receiver1, receiver2, receiver3, ... [tab] [Actual message]
		 */

		/* NOTE: Must make private fields in ClientObserver public in order to use this
		 * tester and must uncomment print statement in update method
		 */

		/*
		ClientObserver tester1 = new ClientObserver(System.out, "Aaron");
		String message1 = "from:Bob" + "\t" + "to:Aaron, Carl, Dave" + "\t" + "Hello, world!";
		System.out.println("Raw message: " + message1 + "\n");
		tester1.findNames(message1);
		System.out.println("Name: " + tester1.name);
		System.out.println("Sender: " + tester1.unusedSender);
		System.out.print("Receivers: ");
		for (String s : tester1.receivers) {
			System.out.print(s + " ");
		}
		System.out.println("\n");

		ClientObserver tester2 = new ClientObserver(System.out, "Bob");
		ClientObserver tester3 = new ClientObserver(System.out, "Carl");
		String message2 = "from:Bob" + "\t" + "to:Aaron" + "\t" + "Hello, Aaron!";
		System.out.println("Raw message: " + message2 + "\n");

		System.out.println("Calling update() on Observer Bob (should be blank):");
		tester2.update(null, message2);
		System.out.println("Calling update() on Observer Carl (should be blank):");
		tester3.update(null, message2);
		System.out.println("Calling update() on Observer Aaron (should print the raw message):");
		tester1.update(null, message2);
		*/
	}

}
