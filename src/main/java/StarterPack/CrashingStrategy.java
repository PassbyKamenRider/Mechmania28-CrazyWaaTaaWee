package StarterPack;

import StarterPack.player.CharacterClass;
import StarterPack.player.Item;
import StarterPack.player.Position;

public class CrashingStrategy implements Strategy {
    @Override
    public CharacterClass strategyInitialize() {
        return null;
    }

    @Override
    public Position moveActionDecision(GameState gameState, int myPlayerIndex) {
        return null;
    }

    @Override
    public int attackActionDecision(GameState gameState, int myPlayerIndex) {
        int a = 1 / 0;
        return 0;
    }

    @Override
    public Item buyActionDecision(GameState gameState, int myPlayerIndex) {
        return null;
    }

    @Override
    public boolean useActionDecision(GameState gameState, int myPlayerIndex) {
        return false;
    }
}
