package StarterPack;

import StarterPack.action.AttackAction;
import StarterPack.action.BuyAction;
import StarterPack.action.MoveAction;
import StarterPack.action.UseAction;
import StarterPack.networking.Client;
import StarterPack.networking.CommState;
import StarterPack.networking.Router;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;


public class Main {
    static enum Phase { USE, MOVE, ATTACK, BUY };

    public static final Logger LOGGER = org.apache.logging.log4j.LogManager.getLogger(Main.class.getName());
    static {
        Configurator.setLevel(LogManager.getLogger(Main.class).getName(), Level.DEBUG);
    }
    public static void main(String[] args) {
        // Establish connection, send Ping message

        Strategy strategy = new RandomStrategy();
        int playerIndex = -1;

        Client client = new Client(Integer.parseInt(args[0]));
        while (!client.isConnected()) {
            client.connect();
            if(!client.isConnected()) {
                try {
                    Thread.sleep(1000);
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
                    LOGGER.debug("Received player index: " + read);

                    if (playerIndex == 0) {
                        strategy = new CrashingStrategy();
                    }
                    commState = CommState.CLASS_REPORT;
                    break;
                case CLASS_REPORT:

                    try {
                        LOGGER.debug(new ObjectMapper().writeValueAsString(strategy.strategyInitialize()));
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }

                    client.write(
                            strategy.strategyInitialize());
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
            LOGGER.debug(gameStateString);

            if (gameStateString.equals("fin")) {
                LOGGER.debug("fin");
                break;
            }

            GameState gameState = Router.parseMessageAsGameState(gameStateString);

            ObjectMapper objectMapper = new ObjectMapper();
            String resultString = "";
            switch (phase) {
                case USE:
                    try {
                        resultString = objectMapper.writeValueAsString(new UseAction(playerIndex,
                                strategy.useActionDecision(gameState, playerIndex)));
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        client.disconnect();
                        e.printStackTrace();
                    }

                    phase = Phase.MOVE;
                    break;
                case MOVE:
                    try {
                        resultString = objectMapper.writeValueAsString(new MoveAction(playerIndex,
                                strategy.moveActionDecision(gameState, playerIndex)));
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        client.disconnect();
                        e.printStackTrace();
                    }
                    phase = Phase.ATTACK;
                    break;
                case ATTACK:
                    try {
                        resultString = objectMapper.writeValueAsString(new AttackAction(playerIndex,
                                strategy.attackActionDecision(gameState, playerIndex)));
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        client.disconnect();
                        e.printStackTrace();
                    }
                    phase = Phase.BUY;
                    break;
                case BUY:
                    try {
                        resultString = objectMapper.writeValueAsString(new BuyAction(playerIndex,
                                strategy.buyActionDecision(gameState, playerIndex)));
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        client.disconnect();
                        e.printStackTrace();
                    }
                    phase = Phase.USE;
                    break;
            }
            LOGGER.debug(resultString);
            client.write(resultString);


        }

        client.disconnect();

    }
}
