import java.net.ServerSocket;
import java.net.Socket;
public class Server {
	
	/**
	 * @param args
	 */
	public static void main(String[] args)throws Exception {
		ServerSocket welcomeSocket = new ServerSocket(6780);
		while (true) {
			Socket connectionSocket = welcomeSocket.accept();
			(new ServerThread1(connectionSocket)).start();
		}
	}
}
