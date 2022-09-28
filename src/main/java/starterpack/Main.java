package starterpack;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;
import starterpack.action.AttackAction;
import starterpack.action.BuyAction;
import starterpack.action.MoveAction;
import starterpack.action.UseAction;
import starterpack.game.GameState;
import starterpack.networking.Client;
import starterpack.networking.CommState;
import starterpack.networking.Router;
import starterpack.strategy.Strategy;
import starterpack.strategy.StrategyConfig;


public class Main {
    static enum Phase { USE, MOVE, ATTACK, BUY };

    private static final Logger LOGGER = LogManager.getLogger(Main.class.getName());

    public static void main(String[] args) {
        // Establish connection, send Ping message

        if (args.length >= 2 && args[1].equals("debug")) {
            Configurator.setLevel(LogManager.getLogger(Main.class).getName(), Level.DEBUG);
            Configurator.setLevel(LogManager.getLogger(Client.class).getName(), Level.DEBUG);
        } else {
            Configurator.setLevel(LogManager.getLogger(Main.class).getName(), Level.INFO);
            Configurator.setLevel(LogManager.getLogger(Client.class).getName(), Level.INFO);
        }



        int playerIndex = -1;

        if (args.length >= 1) {

            try {
                playerIndex = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                LOGGER.debug("Invalid player number.");
                return;
            }

            if (playerIndex < 0 || playerIndex >= Config.MAX_PLAYERS) {
                LOGGER.debug("Player number out of bounds.");
                return;
            }
        } else {
            LOGGER.debug("Please specify a player number.");
        }

        Strategy strategy = StrategyConfig.getStrategy(playerIndex);

        Client client = new Client(Config.PORTS[playerIndex]);
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
