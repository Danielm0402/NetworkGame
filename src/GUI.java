import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.text.*;

public class GUI extends Application {

    public static final int size = 20;
    public static final int scene_height = size * 20 + 100;
    public static final int scene_width = size * 20 + 200;

    public static Image image_floor;
    public static Image image_wall;
    public static Image hero_right, hero_left, hero_up, hero_down;

    public static Player player1;
    public static Player player2;
    public static Player player3;
    public static List<Player> players = new ArrayList<Player>();

    private Label[][] fields;
    private TextArea scoreList;

    private DataOutputStream outToServer;
    private BufferedReader inFromServer;
    private Socket clientSocket = new Socket("10.10.138.62", 8000);

    private String[] board = {    // 20x20
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

    public GUI() throws IOException {
    }


    // -------------------------------------------
    // | Maze: (0,0)              | Score: (1,0) |
    // |-----------------------------------------|
    // | boardGrid (0,1)          | scorelist    |
    // |                          | (1,1)        |
    // -------------------------------------------

    @Override
    public void start(Stage primaryStage) {
        new ReadThread().start();

        try {
            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new Insets(0, 10, 0, 10));

            Text mazeLabel = new Text("Maze:");
            mazeLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));

            Text scoreLabel = new Text("Score:");
            scoreLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));

            scoreList = new TextArea();

            GridPane boardGrid = new GridPane();

            image_wall = new Image(getClass().getResourceAsStream("Image/wall4.png"), size, size, false, false);
            image_floor = new Image(getClass().getResourceAsStream("Image/floor1.png"), size, size, false, false);

            hero_right = new Image(getClass().getResourceAsStream("Image/heroRight.png"), size, size, false, false);
            hero_left = new Image(getClass().getResourceAsStream("Image/heroLeft.png"), size, size, false, false);
            hero_up = new Image(getClass().getResourceAsStream("Image/heroUp.png"), size, size, false, false);
            hero_down = new Image(getClass().getResourceAsStream("Image/heroDown.png"), size, size, false, false);

            fields = new Label[20][20];

            for (int j = 0; j < 20; j++) {
                for (int i = 0; i < 20; i++) {
                    switch (board[j].charAt(i)) {
                        case 'w':
                            fields[i][j] = new Label("", new ImageView(image_wall));
                            break;
                        case ' ':
                            fields[i][j] = new Label("", new ImageView(image_floor));
                            break;
                        default:
                            throw new Exception("Illegal field value: " + board[j].charAt(i));
                    }
                    boardGrid.add(fields[i][j], i, j);
                }
            }
            scoreList.setEditable(false);

            grid.add(mazeLabel, 0, 0);
            grid.add(scoreLabel, 1, 0);
            grid.add(boardGrid, 0, 1);
            grid.add(scoreList, 1, 1);

            Scene scene = new Scene(grid, scene_width, scene_height);
            primaryStage.setScene(scene);
            primaryStage.show();


			int[] player1Coordinates = Server.getPlayer1Coordinates();
            player1 = new Player("Player1", player1Coordinates[0], player1Coordinates[1], "up");
            players.add(player1);
            fields[player1Coordinates[0]][player1Coordinates[1]].setGraphic(new ImageView(hero_up));

			int[] player2Coordinates = Server.getPlayer2Coordinates();
            player2 = new Player("Player2", player2Coordinates[0], player2Coordinates[1], "up");
            players.add(player2);
            fields[player2Coordinates[0]][player2Coordinates[1]].setGraphic(new ImageView(hero_up));

			int[] player3Coordinates = Server.getPlayer3Coordinates();
            player3 = new Player("Player3", player3Coordinates[0], player3Coordinates[1], "up");
            players.add(player3);
            fields[player3Coordinates[0]][player3Coordinates[1]].setGraphic(new ImageView(hero_up));

            scene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
                switch (event.getCode()) {
                    case UP:
                        try {
                            sendInputToServer(0, -1, "up", player3);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        break;
                    case DOWN:
                        try {
                            sendInputToServer(0, +1, "down", player3);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        break;
                    case LEFT:
                        try {
                            sendInputToServer(-1, 0, "left", player3);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        break;
                    case RIGHT:
                        try {
                            sendInputToServer(+1, 0, "right", player3);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        break;
                    default:
                        break;
                }
            });

            scoreList.setText(getScoreList());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void playerMoved(int delta_x, int delta_y, String direction, Player player) {
        Platform.runLater(() -> {
            player.direction = direction;
            int x = player.getXpos(), y = player.getYpos();
            if (board[y + delta_y].charAt(x + delta_x) == 'w') {
                player.addPoints(-1);
            } else {
                Player p = getPlayerAt(x + delta_x, y + delta_y);
                if (p != null) {
                    player.addPoints(10);
                    p.addPoints(-10);
                } else {
                    player.addPoints(1);
                    fields[x][y].setGraphic(new ImageView(image_floor));
                    x += delta_x;
                    y += delta_y;
                    if (direction.equals("right")) {
                        fields[x][y].setGraphic(new ImageView(hero_right));
                    }
                    if (direction.equals("left")) {
                        fields[x][y].setGraphic(new ImageView(hero_left));
                    }
                    if (direction.equals("up")) {
                        fields[x][y].setGraphic(new ImageView(hero_up));
                    }
                    if (direction.equals("down")) {
                        fields[x][y].setGraphic(new ImageView(hero_down));
                    }
                    player.setXpos(x);
                    player.setYpos(y);
                }
            }
            scoreList.setText(getScoreList());
        });

    }

    public String getScoreList() {
        StringBuffer b = new StringBuffer(100);
        for (Player p : players) {
            b.append(p + "\r\n");
        }
        return b.toString();
    }

    public Player getPlayerAt(int x, int y) {
        for (Player p : players) {
            if (p.getXpos() == x && p.getYpos() == y) {
                return p;
            }
        }
        return null;
    }

    public void sendInputToServer(int x, int y, String direction, Player player) throws IOException {
        String input = x + " " + y + " " + direction + " " + player.getName();
        outToServer = new DataOutputStream(clientSocket.getOutputStream());
        outToServer.writeBytes(input + '\n');
        System.out.println("FROM CLIENT: " + input);
    }

    class ReadThread extends Thread {
        private String sentence;

        public void run() {
            try {
                inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                while (true) {
                    try {
                        sentence = inFromServer.readLine();
                        System.out.println(sentence);
                        movePlayer(sentence);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        public void movePlayer(String sentence) {
            String[] str = sentence.split(" ");
            int deltaX;
            int deltaY;
            if (str[2].equals("right")) {
                deltaX = 1;
                deltaY = 0;
            } else if (str[2].equals("left")) {
                deltaX = -1;
                deltaY = 0;
            } else if (str[2].equals("up")) {
                deltaY = -1;
                deltaX = 0;
            } else {
                deltaY = 1;
                deltaX = 0;
            }
            if (str[3].equals("Player1")) {
                playerMoved(deltaX, deltaY, str[2], player1);
            } else if (str[3].equals("Player2")) {
                playerMoved(deltaX, deltaY, str[2], player2);
            } else {
                playerMoved(deltaX, deltaY, str[2], player3);
            }
        }
    }
}