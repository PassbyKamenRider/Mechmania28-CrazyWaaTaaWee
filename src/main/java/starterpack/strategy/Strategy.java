package starterpack.strategy;

import starterpack.game.GameState;
import starterpack.player.CharacterClass;
import starterpack.player.Item;
import starterpack.player.Position;

public interface Strategy {

    public abstract CharacterClass strategyInitialize();

    public abstract Position moveActionDecision(GameState gameState, int myPlayerIndex);

    public abstract int attackActionDecision(GameState gameState, int myPlayerIndex);

    public abstract Item buyActionDecision(GameState gameState, int myPlayerIndex);

    public abstract boolean useActionDecision(GameState gameState, int myPlayerIndex);


}
