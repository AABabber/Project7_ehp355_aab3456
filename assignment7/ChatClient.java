import java.net.Socket;

import javax.swing.JButton;

public class ChatClient {
	private BufferedReader reader;
	private PrintWriter writer;
	private JTextArea incoming;
	private JTextField outgoing;
	
	private void setUpNetworking() throws Exception{
		Socket sock = new Socket("127.0.0.1", 9000);
		InputStreamReader streamReader = new InputStreamReader(sock.getInputStream());
		reader = new  BufferedReader(streamReader);
		writer = new PrintWriter(sock.getOutputStream());
		Thread readerThread = new Thread(new IncomingReader());
		readerThread.start();
	}
	
	private void initView(){
		outgoing = new JTextField(20);
		JButton sendButton = new JButton("Send");
		sendButton.addActionListener(new SendButtonListener());
	}


	
	class SendButtonListener implements ActionListener{
		public void actionPerformed(ActionEvent e){
			writer.println(outgoing.getText());
			writer.flush();
		}
	}
}
