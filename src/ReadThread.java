import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class ReadThread extends Thread {
    Socket connSocket;
    String sentence;

    public ReadThread(Socket connSocket) {
        this.connSocket = connSocket;
    }

    public void run() {

        try {
            BufferedReader inFromX = new BufferedReader(new InputStreamReader(connSocket.getInputStream()));
            while (true) {
                try {
                    sentence = inFromX.readLine();
                    System.out.println(sentence);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
