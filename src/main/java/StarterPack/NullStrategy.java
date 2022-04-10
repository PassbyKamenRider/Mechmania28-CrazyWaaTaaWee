package StarterPack;

import StarterPack.player.CharacterClass;
import StarterPack.player.Item;
import StarterPack.player.Position;

public class NullStrategy implements Strategy {
    public CharacterClass initialClass() {
        return CharacterClass.KNIGHT;
    }

    public Position moveActionDecision(GameState gameState, int myPlayerIndex) {
        return new Position(0, 0);
    }

    public int attackActionDecision(GameState gameState, int myPlayerIndex) {
        return 0;
    }

    public Item buyActionDecision(GameState gameState, int myPlayerIndex) {
        return Item.NONE;
    }

    public boolean useActionDecision(GameState gameState, int myPlayerIndex) {
        return false;
    }
}
