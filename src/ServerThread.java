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
	
	public ServerThread(Socket connSocket) {
		this.connSocket = connSocket;
	}

	public void run() {
		try {
			//this.sleep(10000);
			inFromClient = new BufferedReader(new InputStreamReader(connSocket.getInputStream()));
			outToClient = new DataOutputStream(connSocket.getOutputStream());
			while (true) {
				clientSentence = inFromClient.readLine();
				Server.gennemløb(clientSentence);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} /*catch (InterruptedException e) {
			throw new RuntimeException(e);
		}*/
	}
	public void skrivBytes(String besked) throws IOException {
		System.out.println(besked);

		outToClient.writeBytes(besked + '\n');
	}
}