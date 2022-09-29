package starterpack.strategy;

import starterpack.game.GameState;
import starterpack.game.CharacterClass;
import starterpack.game.Item;
import starterpack.game.Position;

public interface Strategy {

    CharacterClass strategyInitialize();

    Position moveActionDecision(GameState gameState, int myPlayerIndex);

    int attackActionDecision(GameState gameState, int myPlayerIndex);

    Item buyActionDecision(GameState gameState, int myPlayerIndex);

    boolean useActionDecision(GameState gameState, int myPlayerIndex);


}
