package starterpack.strategy;

import starterpack.game.GameState;
import starterpack.player.CharacterClass;
import starterpack.player.Item;
import starterpack.player.Position;

public class InfiniteLoopStrategy implements Strategy{
    @Override
    public CharacterClass strategyInitialize() {
        return CharacterClass.KNIGHT;
    }

    @Override
    public Position moveActionDecision(GameState gameState, int myPlayerIndex) {
        return gameState.getPlayerStateByIndex(myPlayerIndex).getPosition();
    }

    @Override
    public int attackActionDecision(GameState gameState, int myPlayerIndex) {
        int i = 0;
        while(i < 100) {
            i = 0;
        }
        return 0;
    }

    @Override
    public Item buyActionDecision(GameState gameState, int myPlayerIndex) {
        return Item.NONE;
    }

    @Override
    public boolean useActionDecision(GameState gameState, int myPlayerIndex) {
        return false;
    }
}
