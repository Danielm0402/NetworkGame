import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class ServerThread extends Thread{
	Socket connSocket;
	BufferedReader inFromClient;
	DataOutputStream outToClient;
	String clientSentence;
	String navn;
	
	public ServerThread(Socket connSocket) {
		this.connSocket = connSocket;
		navn = this.getName();
	}

	public void run() {
		try {
			inFromClient = new BufferedReader(new InputStreamReader(connSocket.getInputStream()));
			outToClient = new DataOutputStream(connSocket.getOutputStream());
			while (true) {
				clientSentence = inFromClient.readLine();
				Server.gennemløb(clientSentence);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void skrivBytes(String besked) throws IOException {
		System.out.println(besked);

		outToClient.writeBytes(navn.charAt(7) + " " + besked + '\n');
	}
}