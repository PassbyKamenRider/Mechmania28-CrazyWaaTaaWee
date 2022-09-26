package starterpack;

import starterpack.player.CharacterClass;
import starterpack.player.Item;
import starterpack.player.Position;

public class CrashingStrategy implements Strategy {
    @Override
    public CharacterClass strategyInitialize() {
        return CharacterClass.KNIGHT;
    }

    @Override
    public Position moveActionDecision(GameState gameState, int myPlayerIndex) {
        return new Position(0,0);
    }

    @Override
    public int attackActionDecision(GameState gameState, int myPlayerIndex) {
        int a = 1 / 0;
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
