package assignment7;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

public class ServerGUI extends Application {
	
	public Stage theStage;
	public TextArea serverText;
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		theStage = primaryStage;
		serverText = new TextArea();
		serverText.setEditable(false);
		serverText.appendText("Server started...");
		// Create a scene and place it in the stage
		Scene scene = new Scene(new ScrollPane(serverText), 450, 200);
		theStage.setTitle("Program 7 - Server"); // Set the stage title
		theStage.setScene(scene); // Place the scene in the stage
		theStage.setResizable(false);
		theStage.show(); // Display the stage
	}
	
	public void updateText(String text) {
		serverText.appendText(text + "\n");
	}

}
