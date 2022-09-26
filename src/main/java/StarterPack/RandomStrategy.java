package StarterPack;

import StarterPack.player.CharacterClass;
import StarterPack.player.Item;
import StarterPack.player.Position;
import StarterPack.util.Utility;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;

import java.util.concurrent.ThreadLocalRandom;

public class RandomStrategy implements Strategy{

    public static final Logger LOGGER = org.apache.logging.log4j.LogManager.getLogger(Main.class.getName());
    static {
        Configurator.setLevel(LogManager.getLogger(Main.class).getName(), Level.DEBUG);
    }

    public CharacterClass strategyInitialize() {
        return CharacterClass.WIZARD;
    }

    public Position moveActionDecision(GameState gameState, int myPlayerIndex) {
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
            LOGGER.debug("distance of " + i + ": ");
            LOGGER.debug(Utility.squareDistance(
                    gameState.getPlayerStateByIndex(myPlayerIndex).getPosition(),
                    gameState.getPlayerStateByIndex(i).getPosition()));
            LOGGER.debug("my range: " + gameState.getPlayerStateByIndex(myPlayerIndex).getCharacterClass().getStatSet().getRange());

            if (i != myPlayerIndex ) {
                    if (Utility.squareDistance(
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
        return Item.NONE;
    }

    public boolean useActionDecision(GameState gameState, int myPlayerIndex) {
        return false;
    }
}
