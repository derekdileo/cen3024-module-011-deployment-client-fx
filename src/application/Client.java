package application;
	
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;


public class Client extends Application {
	
	// IO Streams for communication to / from server
	DataOutputStream toServer = null;
	DataInputStream fromServer = null;
	
	static TextArea ta;
	
	// Create socket for Client / Server connection
	public static Socket socket = null;
	
	/** Main method calls launch() to start JavaFX GUI.
	 * @param args mandatory parameters for command line method call */
	public static void main(String[] args) {
		launch(args);
	}
	
	// Declare stage (window) outside of start() method
	// so it is accessible to closeProgram() (See line 115)
	protected static Stage window ;
	
	@Override
	public void start(Stage primaryStage) {
		
		// Rename stage to window for sanity
		window = primaryStage;
		
		// Handle close button request. 
		// Launch ConfirmBox to confirm if user wishes to quit
		window.setOnCloseRequest(e -> {
			// Consume the event to allow closeProgram() to do its job
			e.consume();
			closeProgram();
		});

		BorderPane paneForTextField = new BorderPane();
		paneForTextField.setPadding(new Insets(5,5,5,5));
		paneForTextField.setStyle("-fx-border-color: green");
		paneForTextField.setLeft(new Label("Enter a number: "));
		
		TextField tf = new TextField();
		tf.setAlignment(Pos.BOTTOM_RIGHT);
		paneForTextField.setCenter(tf);
		
		BorderPane mainPane = new BorderPane();
		
		// Text area to display contents
		ta = new TextArea();
		mainPane.setCenter(new ScrollPane(ta));
		mainPane.setTop(paneForTextField);
		
		// Create a scene and place it in the stage
		Scene scene = new Scene(mainPane, 450, 200);
		window.setTitle("Client");
		window.setScene(scene);
		window.show();
		
		tf.setOnAction(e -> {
			try {
				// Get user entry from the text field
				int entry = Integer.parseInt(tf.getText().trim());
				
				System.out.println("User entered: " + entry);
				// Send user entry to the server
				toServer.writeInt(entry);
				toServer.flush();
				
				tf.clear();
				
				// Get response from server
				String response = fromServer.readUTF();
				
				// Display response to the text area
				ta.appendText(response + '\n');
				
			} catch (IOException ex) {
				ta.appendText(ex.toString() + '\n');
				System.err.println(ex);
				System.out.println("IOException at tf.setOnAction() " + ex.getMessage());
				ex.printStackTrace();
			}
			
		});
		
		try {
			// Create a socket to connect to the server
			socket = new Socket("localhost", 8000); 	
			// Socket socket = new Socket("130.254.204.36", 8000);
			// Socket socket = new Socket("drake.Armstrong.edu", 8000);
			
			// Create an input stream to receive data from the server
			fromServer = new DataInputStream(socket.getInputStream());
				
			// Create an output stream to send data to the server
			toServer = new DataOutputStream(socket.getOutputStream());
				
			
		} catch (IOException ex) {
			ta.appendText(ex.toString() + '\n');
		}
		
	}

	/* closeProgram() Method uses ConfirmBox class to confirm if user wants to quit */
	private void closeProgram() {
		Boolean answer = ConfirmBox.display("", "Are you sure you want to quit?");
		
		if(answer) {
			// Close socket and window, then exit.
			try {
				ta.appendText("Window Closed!");
				socket.close();
				window.close();			
			} catch (Exception e) {
				System.out.println(e.getMessage());
				e.printStackTrace();
			}
			
		}
		
	}
	
}
