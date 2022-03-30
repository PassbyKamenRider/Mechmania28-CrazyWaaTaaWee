package StarterPack;

import StarterPack.action.*;
import StarterPack.networking.Client;
import StarterPack.networking.CommState;
import StarterPack.networking.Router;
import StarterPack.player.CharacterClass;
import StarterPack.player.Position;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Random;

public class Main {
    static enum Phase { USE, MOVE, ATTACK, BUY };

    public static void main(String[] args) {
        // Establish connection, send Ping message

        Strategy strategy = new Strategy();
        int playerIndex = -1;

        Client client = new Client(Integer.parseInt(args[0]));
        while (!client.isConnected()) {
            client.connect();
            if(!client.isConnected()) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {}
            }
        }

        CommState commState = CommState.START;
        while (commState != CommState.END) {
            switch (commState) {
                case START:
                    while (!client.read().equals("wake")) continue;
                    commState = CommState.NUM_ASSIGN;
                    break;
                case NUM_ASSIGN:
                    String read = client.read();
                    playerIndex = Integer.parseInt(read);
                    System.out.println("Received player index: " + read);
                    commState = CommState.CLASS_REPORT;
                    break;
                case CLASS_REPORT:
                    client.write(
                            CharacterClass.values()[new Random().nextInt(CharacterClass.values().length)]);
                    commState = CommState.IN_GAME;
                    break;
                case IN_GAME:
                    commState = CommState.END;
                    break;
            }
        }


        String gameStateString;
        Phase phase = Phase.USE;
        while (true) {

            gameStateString = client.read();
            System.out.println(gameStateString);

            if (gameStateString.equals("fin")) {
                System.out.println("fin");
                break;
            }

            GameState gameState = Router.parseMessageAsGameState(gameStateString);

            ObjectMapper objectMapper = new ObjectMapper();
            String resultString = "";
            switch (phase) {
                case USE:
                    try {
                        resultString = objectMapper.writeValueAsString(new UseAction(playerIndex));
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                    break;
                case MOVE:
                    try {
                        resultString = objectMapper.writeValueAsString(new MoveAction(
                                playerIndex, new Position(0, 0)));
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                    break;
                case ATTACK:
                    try {
                        resultString = objectMapper.writeValueAsString(new AttackAction(playerIndex, 0));
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                    break;
                case BUY:
                    try {
                        resultString = objectMapper.writeValueAsString(new BuyAction(playerIndex));
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                    break;
            }

            client.write(resultString);

        }

        client.disconnect();

    }
}
