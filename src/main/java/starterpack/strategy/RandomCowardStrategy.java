package starterpack.strategy;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;
import starterpack.Config;
import starterpack.game.GameState;
import starterpack.game.CharacterClass;
import starterpack.game.Item;
import starterpack.game.PlayerState;
import starterpack.game.Position;
import starterpack.util.Utility;

import java.util.concurrent.ThreadLocalRandom;

public class RandomCowardStrategy implements Strategy {

    public static final Logger LOGGER = LogManager.getLogger(RandomCowardStrategy.class.getName());
    static {
        Configurator.setLevel(LogManager.getLogger(RandomCowardStrategy.class).getName(), Level.INFO);
    }

    public CharacterClass strategyInitialize() {
        return CharacterClass.WIZARD;
    }

    public Position moveActionDecision(GameState gameState, int myPlayerIndex) {
        PlayerState playerState = gameState.getPlayerStateByIndex(myPlayerIndex);
        if (playerState.getHealth() < playerState.getStatSet().getMaxHealth()) {
            int dist = 100;
            Position result;
            for (int x = 0; x < Config.BOARD_SIZE; x++) {
                for (int y = 0; y < Config.BOARD_SIZE; y++) {
                    if (Utility.chebyshevDistance(playerState.getPosition(), new Position(x, y)) < playerState.getStatSet().getSpeed())  {

                    }
                }
            }
        }
        while (true) {
            int randomX = ThreadLocalRandom.current().nextInt(0, Config.BOARD_SIZE + 1);
            int randomY = ThreadLocalRandom.current().nextInt(0, Config.BOARD_SIZE + 1);

            if (Utility.manhattanDistance(new Position(randomX, randomY),
                    gameState.getPlayerStateByIndex(myPlayerIndex).getPosition())
                    <= gameState.getPlayerStateByIndex(myPlayerIndex).getCharacterClass().getStatSet().getSpeed()) {
                return new Position(randomX, randomY);
            }
        }
    }

    public int attackActionDecision(GameState gameState, int myPlayerIndex) {
        int res = 0;
        for (int i = 0; i < 4; i++) {
            
            if (i != myPlayerIndex) {
                if (Utility.chebyshevDistance(
                        gameState.getPlayerStateByIndex(myPlayerIndex).getPosition(),
                        gameState.getPlayerStateByIndex(i).getPosition()) <=
                        gameState.getPlayerStateByIndex(myPlayerIndex).getCharacterClass().getStatSet().getRange()) {
                    return i;
                }
                res = i;
            }
        }
        return res;
    }

    public Item buyActionDecision(GameState gameState, int myPlayerIndex) {
        return Utility.randomEnum(Item.class);
    }

    public boolean useActionDecision(GameState gameState, int myPlayerIndex) {
        return true;
    }
}
