import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {

	private static ArrayList<ServerThread> list = new ArrayList<>();


	private static String[] board = {    // 20x20
			"wwwwwwwwwwwwwwwwwwww",
			"w        ww        w",
			"w w  w  www w  w  ww",
			"w w  w   ww w  w  ww",
			"w  w               w",
			"w w w w w w w  w  ww",
			"w w     www w  w  ww",
			"w w     w w w  w  ww",
			"w   w w  w  w  w   w",
			"w     w  w  w  w   w",
			"w ww ww        w  ww",
			"w  w w    w    w  ww",
			"w        ww w  w  ww",
			"w         w w  w  ww",
			"w        w     w  ww",
			"w  w              ww",
			"w  w www  w w  ww ww",
			"w w      ww w     ww",
			"w   w   ww  w      w",
			"wwwwwwwwwwwwwwwwwwww"
	};

	private static int[] player1Coordinates = generateRandomCoordinates();
	private static int[] player2Coordinates = generateRandomCoordinates();
	private static int[] player3Coordinates = generateRandomCoordinates();

	public static void main(String[] args)throws Exception {
		ServerSocket welcomeSocket = new ServerSocket(8000);
		while (true) {
			Socket connectionSocket = welcomeSocket.accept();
			ServerThread serverThread = new ServerThread(connectionSocket);
			serverThread.start();
			list.add(serverThread);
		}
	}

	public synchronized static void gennemløb(String besked) throws IOException {
		for (ServerThread s : list){
			s.skrivBytes(besked);
		}
	}

	public static int[] generateRandomCoordinates(){
		int[] coordinates = {-1, -1};
		boolean validCoordinates = false;
		while (!validCoordinates){
			int i = (int) (1 + Math.random() * (20 -1));
			int j = (int) (1 + Math.random() * (20 - 1));
			if (board[j].charAt(i) != 'w'){
				coordinates[0] = i;
				coordinates[1] = j;
				validCoordinates = true;
			}
		}
		return coordinates;
	}

	public static int[] getPlayer1Coordinates() {
		return player1Coordinates;
	}

	public static int[] getPlayer2Coordinates() {
		return player2Coordinates;
	}

	public static int[] getPlayer3Coordinates() {
		return player3Coordinates;
	}
}
