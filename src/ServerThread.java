import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class ServerThread extends Thread {
	Socket connSocket;
	BufferedReader inFromClient;
	DataOutputStream outToClient;
	String clientSentence;

	public ServerThread(Socket connSocket) {
		this.connSocket = connSocket;
	}

	public void run() {
		try {
			inFromClient = new BufferedReader(new InputStreamReader(connSocket.getInputStream()));
			outToClient = new DataOutputStream(connSocket.getOutputStream());
			while (true) {
				clientSentence = inFromClient.readLine();
				skrivBytes();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void skrivBytes() throws IOException {
		if (clientSentence != null) {
			System.out.println(clientSentence);
			outToClient.writeBytes("ecco " + clientSentence.toUpperCase() + '\n');
		}
	}
}