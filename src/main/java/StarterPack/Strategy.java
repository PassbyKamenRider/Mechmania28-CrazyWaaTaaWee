package StarterPack;

import StarterPack.player.CharacterClass;
import StarterPack.player.Item;
import StarterPack.player.Position;

public interface Strategy {

    public abstract CharacterClass initialClass();

    public abstract Position moveActionDecision(GameState gameState, int myPlayerIndex);

    public abstract int attackActionDecision(GameState gameState, int myPlayerIndex);

    public abstract Item buyActionDecision(GameState gameState, int myPlayerIndex);

    public abstract boolean useActionDecision(GameState gameState, int myPlayerIndex);

}
