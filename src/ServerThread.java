import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class ServerThread extends Thread{
	Socket connSocket;
	
	public ServerThread(Socket connSocket) {
		this.connSocket = connSocket;
	}

	public void run() {
		try {
			BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connSocket.getInputStream()));
			DataOutputStream outToClient = new DataOutputStream(connSocket.getOutputStream());
			while (true) {
				String clientSentence = inFromClient.readLine();
				System.out.println(clientSentence);
				outToClient.writeBytes("ecco " + clientSentence.toUpperCase() + '\n');
			}
		} catch (IOException e) {
			e.printStackTrace();
		}		
		// do the work here
	}
}