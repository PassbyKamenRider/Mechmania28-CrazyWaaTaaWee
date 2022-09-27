package starterpack.networking;

import starterpack.game.GameState;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class Router {
//    private Client client;
//    private int RETRY_MS = 1;
//    ObjectMapper objectMapper;
//    private Integer playerNumber;
//    CommState commState;
//
//    public Router(int gamePort) {
//        client = new Client(gamePort);
//        objectMapper = new ObjectMapper();
//        commState = CommState.START;
//    }
//
//    public void connect() {
//        while (!client.isConnected()) {
//            client.connect();
//            if(!client.isConnected()) {
//                try {
//                    Thread.sleep(RETRY_MS);
//                } catch (InterruptedException e) {}
//            }
//        }
//    }
//
//
//    /**
//     * Sets up the game by providing the server your initial class name.
//     * @param characterClass Your choice of initial class.
//     */
//    public void pingEngine(CharacterClass characterClass) {
//
//        while (commState != CommState.END) {
//            switch (commState) {
//                case START:
//                    while (!client.read().equals("wake")) continue;
//                    System.out.println("recieved server ping");
//                    commState = CommState.NUM_ASSIGN;
//                    break;
//                case NUM_ASSIGN:
//                    // Wait for a single digit number.
//                    String read = client.read();
//                    System.out.println(read);
//                    playerNumber = Integer.parseInt(read);
//                    System.out.println("recieved number");
//                    commState = CommState.CLASS_REPORT;
//                    break;
//                case CLASS_REPORT:
//                    client.write(characterClass);
//                    commState = CommState.IN_GAME;
//                    break;
//                case IN_GAME:
//                    commState = CommState.END;
//                    break;
//            }
//        }
//    }
//
//
//    /**
//     * return the GameState object for each phase.
//     * @return the GameState object.
//     */
//    public GameState receiveGameState() {
//        return parseMessageAsGameState(read());
//    }
//
//    /**
//     *
//     * @return
//     */
//    public int getPlayerNumber() {
//        return playerNumber;
//    }
//
//    /**
//     * You should decide if you want to use the item you currently have and call this function each turn.
//     * @param useItem You want (true) or do not want to use (false) to use the item you currently have.
//     */
//    public void sendUseAction(boolean useItem) {
//        sendAction(new UseAction(playerNumber));
//    }
//
//    /**
//     * You should decide a position to move to and call this function each turn.
//     * @param position The position you want to move to.
//     */
//    public void sendMoveAction(Position position) {
//        sendAction(new MoveAction(playerNumber, position));
//    }
//
//    /**
//     * You should decide a player to attack and call this function each turn.
//     * @param targetPlayerNumber The player you want to attack.
//     */
//    public void sendAttackAction(int targetPlayerNumber) {
//        sendAction(new AttackAction(playerNumber, targetPlayerNumber));
//    }
//
//    /**
//     * You should decide if you want to buy any items and call this function each turn.
//     * @param item The item you want to buy.
//     */
//    public void sendBuyAction(Item item) {
//        sendAction(new BuyAction(playerNumber, item));
//    }
//
//    public void cleanUp() {
//        client.disconnect();
//    }
//
//    public boolean isConnected() {
//        return client.isConnected();
//    }
//
//    /*
//    helper functions.
//     */
//
//
//    public void sendAction(Action action) {
//        try {
//            client.write(objectMapper.writeValueAsString(action));
//        } catch (JsonProcessingException e) {
//            e.printStackTrace();
//        }
//    }
//
//    public String read() {
//        String message;
//        while (true) {
//            message = client.read();
//            if (message != null) {
//                break;
//            }
//
//            try {
//                Thread.sleep(RETRY_MS);
//            } catch (InterruptedException e) {}
//        }
//        return message;
//    }

    public static GameState parseMessageAsGameState(String message) {

        GameState result;
        try {
            result = new ObjectMapper().readValue(message, GameState.class);
            return result;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

}
