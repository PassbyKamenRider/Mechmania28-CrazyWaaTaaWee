package StarterPack.player;

/** Represents the entire state of a Player. */
public class PlayerState {
  private CharacterClass characterClass;
  private Item item;
  private Position position;

  public PlayerState(CharacterClass characterClass, Position position) {
    this.characterClass = characterClass;
    this.position = position;
  }

  public PlayerState() {

  }

  public void setItem(Item item) {
    this.item = item;
  }

  public Item getItem() {
    return item;
  }


  public Position getPosition() {
    return position;
  }

  public CharacterClass getCharacterClass() {
    return characterClass;
  }
}
