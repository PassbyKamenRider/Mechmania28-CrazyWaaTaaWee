package starterpack.player;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/** Represents the entire state of a Player. */
@JsonIgnoreProperties(ignoreUnknown = true)
public class PlayerState {
  @JsonProperty("class")
  private CharacterClass characterClass;

  @JsonProperty("item")
  private Item item = Item.NONE;

  @JsonProperty("position")
  private Position position;

  @JsonProperty("gold")
  private int gold;

  @JsonProperty("score")
  private int score;

  @JsonIgnore private int effectTimer;

  @JsonProperty("health")
  private int health;

  @JsonCreator
  public PlayerState(
          @JsonProperty("class") CharacterClass characterClass,
          @JsonProperty("position") Position position) {
    this.characterClass = characterClass;
    this.position = position;
  }

  public Item getItem() {
    return this.item;
  }

  public void setItem(Item item) {
    this.item = item;
  }

  public CharacterClass getCharacterClass() {
    return this.characterClass;
  }

  public void setCharacterClass(CharacterClass characterClass) {
    this.characterClass = characterClass;
  }

  public int getGold() {
    return gold;
  }

  public void setGold(int amount) {
    gold = amount;
  }

  public void incrementGold(int amount) {
    gold += amount;
  }

  public void decrementGold(int amount) {
    gold -= amount;
    if (gold < 0) gold = 0;
  }

  public int getEffectTimer() {
    return this.effectTimer;
  }

  public void setEffectTimer(int effectTimer) {
    this.effectTimer = effectTimer;
  }

  public void decrementEffectTimer() {
    this.effectTimer --;
  }

  public void incrementCurrHealth(int amount) {
    health += amount;
    health %= computeEffectiveStatSet().getMaxHealth();
  }

  /**
   * Returns the effective {@link StatSet} of the player, defined as the base StatSet of their
   * {@link CharacterClass} and the buff/debuff StatSet of their active {@link Item}.
   *
   * @return Effective StatSet.
   */
  public StatSet computeEffectiveStatSet() {
    // Item is either permanent or the buff is still in effect
    if (this.effectTimer != 0) {
      return characterClass.getStatSet().plus(item.getStatSet());
    }
    // No effective item
    else {
      return characterClass.getStatSet();
    }
  }

  public Position getPosition() {
    return position;
  }

  public void setPosition(Position position) {
    this.position = position;
  }

  public void incrementScore() {
    score++;
  }
}