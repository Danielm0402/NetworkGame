import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {

	private static ArrayList<ServerThread> list = new ArrayList<>();

	public static void main(String[] args)throws Exception {
		ServerSocket welcomeSocket = new ServerSocket(6012);
		while (true) {
			Socket connectionSocket = welcomeSocket.accept();
			ServerThread serverThread = new ServerThread(connectionSocket);
			serverThread.start();
			list.add(serverThread);
			int index = list.size() - 1;
			assignPlayers(index, serverThread);
		}
	}

	public static void gennemløb(String besked) throws IOException {
		for (ServerThread s : list){
			s.skrivBytes(besked);
		}
	}

	public static void assignPlayers(int index, ServerThread serverThread){
		serverThread.setPlayer(GUI.players.get(index));
	}
}
