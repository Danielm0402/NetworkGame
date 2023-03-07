

import javafx.application.Application;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class App {

	public static void main(String[] args) throws IOException {
		String modifiedSentence;
		BufferedReader fromGame = new BufferedReader(new InputStreamReader(System.in));
		Socket clientSocket = new Socket("localhost",6780);
		DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
		BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		String sentence = fromGame.readLine();
		outToServer.writeBytes(sentence + '\n');
		modifiedSentence = inFromServer.readLine();
		System.out.println("FROM SERVER: " + modifiedSentence);
		clientSocket.close();
		Application.launch(GUI.class);
	}
}
