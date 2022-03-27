package StarterPack;

import StarterPack.player.PlayerState;

import java.util.Arrays;
import java.util.List;

public class GameState {

  public GameState() {}

  /** Holds the {@link PlayerState} of each of the 4 players in player order. */
  private final List<PlayerState> playerStateList = Arrays.asList(new PlayerState[4]);

  public List<PlayerState> getPlayerStateList() {
    return playerStateList;
  }

  /** Constructor that takes a list of playerStates. */
  public GameState(List<PlayerState> players) {
    playerStateList.set(0, players.get(0));
    playerStateList.set(1, players.get(1));
    playerStateList.set(2, players.get(2));
    playerStateList.set(3, players.get(3));
  }

}
