package starterpack.game;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import starterpack.player.CharacterClass;
import starterpack.player.PlayerState;
import starterpack.player.Position;

import java.util.Arrays;
import java.util.List;

import static starterpack.Config.BOARD_SIZE;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GameState {

  /** Holds the {@link PlayerState} of each of the 4 players in player order. */
  @JsonProperty("player_states")
  private final List<PlayerState> playerStateList = Arrays.asList(new PlayerState[4]);

  /** Holds the {@link Position} (spawn point) of each of the 4 players in player order CW. */
  private final List<Position> spawnPoints = Arrays.asList(
          new Position(0, 0),
          new Position(BOARD_SIZE-1, 0),
          new Position(BOARD_SIZE-1, BOARD_SIZE-1),
          new Position(0, BOARD_SIZE-1)
  );

  @JsonProperty
  private int turn;

  public int getTurn() {
    return turn;
  }

  public void setTurn(int turn) {
    this.turn = turn;
  }

  public GameState() {
  }

  /**
   * Constructor that takes a list of character classes for each of the players respectively.
   *
   * @param playerClasses List of character classes.
   */
  public GameState(List<CharacterClass> playerClasses) {
    for (int i = 0; i < 4; i++) {
      playerStateList.set(i, new PlayerState(playerClasses.get(i), spawnPoints.get(i)));
    }
  }

  public PlayerState getPlayerStateByIndex(int index) {
    return playerStateList.get(index);
  }






}
