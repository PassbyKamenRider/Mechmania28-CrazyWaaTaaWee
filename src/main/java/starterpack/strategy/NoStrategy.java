package starterpack.strategy;
import java.util.stream.*;

import starterpack.Config;
import starterpack.game.*;
import starterpack.util.Utility;
import java.util.*;

public class NoStrategy implements Strategy {
    
    State curr_state;
    Position targetPosition;
    Map<Position, Float> positionMap = new HashMap<Position, Float>();
    Random dice = new Random();

    int num_states = curr_state.values().length;

    //weight_table.get(State s) is an array of ints that contain the weight of transiting from state s to all states.
    //weight is never zero: impossibility is laplace-smoothed to 1.
    Hashtable<State,Float[]> weight_table = new Hashtable<State, Float[]>();

    public CharacterClass strategyInitialize(int myPlayerIndex) {
        //initializes weight_table
        for (State s : State.values()) {
            Float[] weights = new Float[num_states];
            Arrays.fill(weights, 1.0f/num_states);
            weight_table.put(s,weights);
        }
        curr_state = State.ZD;
        if (myPlayerIndex == 0) {
            return Utility.randomEnum(CharacterClass.class);
        } else if (myPlayerIndex == 1 || myPlayerIndex == 2) {
            return CharacterClass.KNIGHT;
        } else {
            return Utility.randomEnum(CharacterClass.class);
        }
    }

    public boolean useActionDecision(GameState gameState, int myPlayerIndex) {
        updateState(gameState, myPlayerIndex);
        return true;
    }

    public Position moveActionDecision(GameState gameState, int myPlayerIndex) {
        updateState(gameState, myPlayerIndex);
        if (curr_state == State.RUN) {
            targetPosition = Utility.spawnPoints.get(myPlayerIndex);
        } else if (curr_state == State.ZD) {
            ZD(gameState, myPlayerIndex);
        } else {
            Kite(gameState, myPlayerIndex);
        }
        return targetPosition;
    }

    public int attackActionDecision(GameState gameState, int myPlayerIndex) {
        updateState(gameState, myPlayerIndex);
        return attack(gameState, myPlayerIndex);
    }

    public Item buyActionDecision(GameState gameState, int myPlayerIndex) {
        if (myPlayerIndex == 0) {
            if (getClassCount(CharacterClass.KNIGHT, gameState, myPlayerIndex) >= 2) {
                return Item.MAGIC_STAFF;
            }
            if (getClassCount(CharacterClass.WIZARD, gameState, myPlayerIndex) >= 2) {
                return Item.STEEL_TIPPED_ARROW;
            }
            if (getClassCount(CharacterClass.ARCHER, gameState, myPlayerIndex) >= 2) {
                return Item.HEAVY_BROADSWORD;
            }
        }
        if (gameState.getPlayerStateByIndex(myPlayerIndex).getCharacterClass() == CharacterClass.ARCHER) {
            return Item.STRENGTH_POTION;
        }
        return Item.SPEED_POTION;
    }

    /* unfinished, main logic
    goes over the current gamestate and therefrom updates transition edge weights in a complete-graph NFA of character states
    uses weighed random (?) to do the transition
    i: the current gameState, playerIndex
    o: none
    s: changes current character state

    author: bucketfish
    */
    public void updateState(GameState gameState, int myPlayerIndex) {
        dangerMap(gameState,myPlayerIndex);
        Float branch_factor = (float)0.4;
        Float map_factor = (float)1/75;
        //get smoothed weights from last update
        Float[] weights = weight_table.get(curr_state);

        PlayerState myState = gameState.getPlayerStateByIndex(myPlayerIndex);
        Position myPosition = myState.getPosition();
        StatSet myStats = myState.getStatSet();
        Integer myAttackRange = myStats.getRange();
        Integer myAttackDamage = myStats.getDamage();
        Integer myMoveSpeed = myStats.getSpeed();
        Integer myHealth = myState.getHealth();
        Float weight_sum,dice_cast,acc_weight;

        switch(curr_state) {
            case ZD:
                weights[curr_state.getNumVal()] += (0.4f*myHealth)*branch_factor;
                if (is_Control(myPosition)) {
                    weights[curr_state.getNumVal()] += branch_factor;
                }

                int dangerCount = 0;
                int mediumCount = 0;

                for (int row = 0; row < 10; row++) {
                    for (int col = 0; col < 10; col++) {

                        if (in_moveRange(new Position(row,col),myPosition,myMoveSpeed)) {
                            Float danger = positionMap.getOrDefault(new Position(row,col), 0f);
                            if (map_factor*danger >= myHealth) {
                                weights[State.RUN.getNumVal()] += 0.1f * branch_factor;
                                dangerCount++;
                            } else if (0.5f*map_factor*danger >= myHealth) {
                                mediumCount++;
                            }
                        }

                        Float runWeight;
                        if (gameState.getPlayerStateByIndex(myPlayerIndex).getCharacterClass() == CharacterClass.KNIGHT) {
                            runWeight = 0.75f;
                        } else if (gameState.getPlayerStateByIndex(myPlayerIndex).getCharacterClass() == CharacterClass.WIZARD) {
                            runWeight = 0.35f;
                        } else {
                            runWeight = 0.35f;
                        }
                        if (dangerCount >= runWeight * (myMoveSpeed+1)*(myMoveSpeed+1)) {
                            weights[State.RUN.getNumVal()] += branch_factor;
                        } else if (mediumCount >= 0.5 * (myMoveSpeed+1)*(myMoveSpeed+1)) {
                            weights[State.KITE.getNumVal()] += 0.5f*branch_factor;
                        }

                        if (is_Control(new Position(row,col))) {
                            Float danger = positionMap.getOrDefault(new Position(row,col), 0f);
                            if (map_factor*danger >= myHealth) {
                                weights[State.KITE.getNumVal()] += 0.2f * branch_factor;
                            }
                        }
                    }
                }
                break;
            case RUN:
                if (myPosition.equals(Utility.spawnPoints.get(myPlayerIndex))) {
                    weights[State.ZD.getNumVal()] += 10*branch_factor;
                } else {

                    int danger_count = 0;
                    for (int row = 0; row < 10; row++) {
                        for (int col = 0; col < 10; col++) {

                            //int mediumCount = 0;
                            if (in_moveRange(new Position(row, col), myPosition, myMoveSpeed)) {
                                Float danger = positionMap.getOrDefault(new Position(row, col), 0f);
                                if (map_factor * danger >= myHealth) {
                                    weights[State.RUN.getNumVal()] += 0.1f * branch_factor;
                                    danger_count++;
                                }
                            }
                        }
                    }

                    if (danger_count <= 0.5f * (myMoveSpeed+1)*(myMoveSpeed+1)) {
                        weights[State.ZD.getNumVal()] += 5*branch_factor;
                        weights[State.KITE.getNumVal()] += 2*branch_factor;
                    } else {
                        weights[State.RUN.getNumVal()] += 10*branch_factor;
                    }
                }
                break;
            case KITE:
                weights[State.ZD.getNumVal()] += 10*branch_factor;
                break;
        }

        //smooth out weights into [0.0,1.0]
        weight_sum = sum(weights);
        for (int i = 0; i < weights.length; i++) {
            weights[i] /= weight_sum;
        }
        weight_table.put(curr_state, weights);
        //randomize and update curr_state; die is cast in [0.0,1.0]
        dice_cast = dice.nextFloat();
        acc_weight = (float)0;
        for (int i = 0; i < weights.length; i++) {
            acc_weight += weights[i];
            if (acc_weight >= dice_cast) {
                curr_state = curr_state.num(i);
                break;
            }
        }

    }

    public Float min(Float[] arr) {
        Float m = arr[0];
        for (Float f : arr) {
            if (f <= m) m = f;
        }
        return m;
    }

    public Float sum(Float[] arr) {
        Float m = (float)0;
        for (Float f : arr) {
            m+=f;
        }
        return m;
    }
    public boolean is_Control(Position p) {
        return ((p.getX()==4 || p.getX()==5) && (p.getY()==4 || p.getY()==5));
    }

    public boolean in_moveRange(Position p, Position player, int speed) {
        return ((p.getX()>= player.getX()-speed && p.getX()>= player.getX()+speed)
                && p.getX()>= player.getX()-speed && p.getX()>= player.getX()+speed);
    }

    /*
    updates position map with all go-go's and no-go's, mainly to avoid enemy attack
    i: the current gameState, playerIndex
    o: none
    s: positionMap gets modified

    author: pkr
    */
    public void dangerMap(GameState gameState, int myPlayerIndex) {
        positionMap.clear();
        // Reminder: use getOrDefault for non-found value
        for (int i = 0; i < 4 && i != myPlayerIndex; i++) {
            PlayerState enemyState = gameState.getPlayerStateByIndex(i);
            Position enemyPosition = enemyState.getPosition();
            StatSet enemyStats = enemyState.getStatSet();
            Integer enemyAttackRange = enemyStats.getRange();
            Integer enemyAttackDamage = enemyStats.getDamage();
            Integer enemyMoveSpeed = enemyStats.getSpeed();

            // iterate over all positions enemy can reach
            for (int row = -enemyMoveSpeed; row <= enemyMoveSpeed; row++) {
                for (int col = -enemyMoveSpeed; col <= enemyMoveSpeed; col++) {
                    int newX = enemyPosition.getX() + row;
                    int newY = enemyPosition.getY() + col;
                    if (Math.abs(row) + Math.abs(col) <= enemyMoveSpeed && newX >= 0 && newX < 10 && newY >= 0 && newY < 10) {
                        // enemy position = (newX, newY)
                        // iterate over all positions in enemy attack range
                        // weighted by enemy damage
                        for (int width = -enemyAttackRange; width <= enemyAttackRange; width++) {
                            for (int height = -enemyAttackRange; height <= enemyAttackRange; height++) {
                                int attackX = newX + width;
                                int attackY = newY + height;
                                // update weight
                                if (attackX >= 0 && attackX < 10 && attackY >= 0 && attackY < 10) {
                                    Float weight = positionMap.getOrDefault(new Position(attackX, attackY), 0f) + enemyAttackDamage;
                                    positionMap.put(new Position(attackX, attackY), weight);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // update target position
    // must move to the center tiles
    // if equal distance, move to the safest one
    public void ZD(GameState gameState, int myPlayerIndex) {
        PlayerState myState = gameState.getPlayerStateByIndex(myPlayerIndex);
        Position myPosition = myState.getPosition();
        StatSet myStats = myState.getStatSet();
        int myMoveSpeed = myStats.getSpeed();
        int currDistance = Utility.manhattanDistance(myPosition, getClosestTile(gameState, myPosition));
        Float minDanger = -1f;

        // iterate over all positions the player can reach
        for (int row = -myMoveSpeed; row <= myMoveSpeed; row++) {
            for (int col = -myMoveSpeed; col <= myMoveSpeed; col++) {
                int moveDistance = Math.abs(row) + Math.abs(col);
                int newX = myPosition.getX() + row;
                int newY = myPosition.getY() + col;
                Position newMyPosition = new Position(newX, newY);
                // all possible player position (in bound)
                if (moveDistance <= myMoveSpeed
                        && newX >= 0 && newX <= Config.BOARD_SIZE
                        && newY >= 0 && newY <= Config.BOARD_SIZE) {
                    int newDistance = Utility.manhattanDistance(newMyPosition, getClosestTile(gameState, newMyPosition));
                    // distance to center tiles > movespeed
                    // move *movespeed* tiles closer
                    if (currDistance > myMoveSpeed) {
                        if (currDistance - newDistance == myMoveSpeed) {
                            Float newDanger = positionMap.getOrDefault(newMyPosition, 0f);
                            if (minDanger == -1f) {
                                targetPosition = newMyPosition;
                                minDanger = newDanger;
                            } else if (newDanger <= minDanger) {
                                targetPosition = newMyPosition;
                                minDanger = newDanger;
                            }
                        }
                    } else if (currDistance <= myMoveSpeed) {
                        // distance to center tiles <= movespeed
                        // move to most safe center tile
                        targetPosition = getSafestTile(gameState, myPosition);
                    }
                }
            }
        }
    }

    public void Kite(GameState gameState, int myPlayerIndex) {
        PlayerState myState = gameState.getPlayerStateByIndex(myPlayerIndex);
        Position myPosition = myState.getPosition();
        StatSet myStats = myState.getStatSet();
        int myMoveSpeed = myStats.getSpeed();
        int myAttackRnage = myStats.getRange();
        Float minDanger = -1f;

        // iterate over all positions the player can reach
        for (int row = -myMoveSpeed; row <= myMoveSpeed; row++) {
            for (int col = -myMoveSpeed; col <= myMoveSpeed; col++) {
                int moveDistance = Math.abs(row) + Math.abs(col);
                int newX = myPosition.getX() + row;
                int newY = myPosition.getY() + col;
                Position newMyPosition = new Position(newX, newY);
                // all possible player position (in bound)
                if (moveDistance <= myMoveSpeed
                        && newX >= 0 && newX <= Config.BOARD_SIZE
                        && newY >= 0 && newY <= Config.BOARD_SIZE) {
                    ArrayList<Integer> attactableEnemies =  getAttactableEnemies(gameState, myPlayerIndex);
                    for (int a = 0; a < attactableEnemies.size(); a++) {
                        if (canAttack(myState, gameState.getPlayerStateByIndex(attactableEnemies.get(a)).getPosition())) {
                            if (minDanger == -1) {
                                minDanger = positionMap.getOrDefault(newMyPosition, 0f);
                                targetPosition = newMyPosition;
                            } else if (positionMap.getOrDefault(newMyPosition, 0f) < minDanger) {
                                targetPosition = newMyPosition;
                            }
                        }
                    }
                }
            }
        }
    }

    public Position getClosestTile(GameState gameState, Position playerPosition) {
        ArrayList<Position> tilesPosition
                = new ArrayList<Position>(Arrays.asList(new Position(4,4), new Position(5,4), new Position(4,5), new Position(5,5)));
        int minDistance = Utility.manhattanDistance(playerPosition, tilesPosition.get(0));
        Position closestPosition = tilesPosition.get(0);
        for (int i = 1; i < tilesPosition.size(); i++) {
            int distance = Utility.manhattanDistance(playerPosition, tilesPosition.get(i));
            if (distance < minDistance) {
                minDistance = distance;
                closestPosition = tilesPosition.get(i);
            }
        }
        return closestPosition;
    }

    public Position getSafestTile(GameState gameState, Position playerPosition) {
        ArrayList<Position> tilesPosition
                = new ArrayList<Position>(Arrays.asList(new Position(4,4), new Position(5,4), new Position(4,5), new Position(5,5)));
        Float minDanger = positionMap.getOrDefault(tilesPosition.get(0), 0f);
        Position safestPosition = tilesPosition.get(0);
        for (int i = 1; i < tilesPosition.size(); i++) {
            Float danger = positionMap.getOrDefault(tilesPosition.get(i), 0f);
            if (danger < minDanger) {
                minDanger = danger;
                safestPosition = tilesPosition.get(i);
            }
        }
        return safestPosition;
    }

    public int attack(GameState gameState, int myPlayerIndex) {
        //If can attack no one return
        if (getAttactableEnemies(gameState, myPlayerIndex) == null) {
            return 0;
        }

        //If can kill one then kill one
        List<Integer> killList = new ArrayList<>();
        for (int i: getAttactableEnemies(gameState, myPlayerIndex)) {
            PlayerState myPlayer = gameState.getPlayerStateByIndex(myPlayerIndex);
            PlayerState enemy = gameState.getPlayerStateByIndex(i);
            if (myPlayer.getStatSet().getDamage() > enemy.getHealth()) {
                killList.add(i);
            }
        }
        //if there are multiple killables kill the one that has most threat;
        if (findMostDangerousPlayer(killList, gameState, myPlayerIndex) != -1) {
            return findMostDangerousPlayer(killList, gameState, myPlayerIndex);
        }

        //If cannot kill attack the one on control tile
        List<Integer> onTileList = new ArrayList<>();
        for (int i: getAttactableEnemies(gameState, myPlayerIndex)) {
            PlayerState myPlayer = gameState.getPlayerStateByIndex(myPlayerIndex);
            PlayerState enemy = gameState.getPlayerStateByIndex(i);
            if (checkOnTile(gameState, i)) {
                onTileList.add(i);
            }
        }
        //If there are multiple on tile kill the one that has the most threat;
        if (findMostDangerousPlayer(onTileList, gameState, myPlayerIndex) != -1) {
            return findMostDangerousPlayer(onTileList, gameState, myPlayerIndex);
        }

        //If cannot kill one attack the one that can attack me;
        List<Integer> attackList = new ArrayList<>();
        for (int i: getAttactableEnemies(gameState, myPlayerIndex)) {
            PlayerState myPlayer = gameState.getPlayerStateByIndex(myPlayerIndex);
            PlayerState enemy = gameState.getPlayerStateByIndex(i);
            if (Utility.chebyshevDistance(enemy.getPosition(), myPlayer.getPosition()) > enemy.getStatSet().getRange()) {
                attackList.add(i);
            }
        }
        if (findMostDangerousPlayer(attackList, gameState, myPlayerIndex) != -1) {
            return findMostDangerousPlayer(attackList, gameState, myPlayerIndex);
        }

        //If cannot kill one attack the one with most damage;
        int result = 0;
        int damage = 0;
        for (int i: getAttactableEnemies(gameState, myPlayerIndex)) {
            PlayerState myPlayer = gameState.getPlayerStateByIndex(myPlayerIndex);
            PlayerState enemy = gameState.getPlayerStateByIndex(i);
            if (enemy.getStatSet().getDamage() > damage) {
                damage = enemy.getStatSet().getDamage();
                result = i;
            }
        }
        return result;
    }

    private ArrayList<Integer> getAttactableEnemies(GameState gameState, int myPlayerIndex) {
        List<Integer> ints = new ArrayList<>(Arrays.asList(new Integer[]{0, 1, 2, 3}));
        List<Integer> result = new ArrayList<>(Arrays.asList(new Integer[]{0, 1, 2, 3}));
        ints.remove(myPlayerIndex);
        result.remove(myPlayerIndex);
        for (Integer i: ints) {
            PlayerState myPlayer = gameState.getPlayerStateByIndex(myPlayerIndex);
            PlayerState enemy = gameState.getPlayerStateByIndex(i);
            if (Utility.chebyshevDistance(enemy.getPosition(), myPlayer.getPosition()) > myPlayer.getStatSet().getRange()) {
                result.remove(i);
            }
        }
        return (ArrayList<Integer>) result;
    }

    private int findMostDangerousPlayer(List<Integer> indexList, GameState gameState, int myPlayerIndex) {
        if (!indexList.isEmpty()) {
            int damage = 0;
            int indexToKill = indexList.get(0);
            for (int i: indexList) {
                PlayerState enemy = gameState.getPlayerStateByIndex(i);
                PlayerState myPlayer = gameState.getPlayerStateByIndex(myPlayerIndex);
                if (Utility.manhattanDistance(enemy.getPosition(), myPlayer.getPosition()) <= enemy.getStatSet().getRange()) {
                    if (damage < enemy.getStatSet().getDamage()) {
                        damage = enemy.getStatSet().getDamage();
                        indexToKill = i;
                    }
                }
            }
            return indexToKill;
        } else {
            return -1;
        }
    }

    private boolean checkOnTile(GameState gameState, int index){
        Position myPosition = gameState.getPlayerStateByIndex(index).getPosition();
        if (myPosition.getX() <= 5 && myPosition.getX() >= 4 && myPosition.getY() <= 5 && myPosition.getY() >= 4) {
            return true;
        } else {
            return false;
        }
    }

    private boolean canAttack(PlayerState playerState, Position position) {
        for(Position position1: getMovePosition(playerState)) {
            if (Utility.chebyshevDistance(position1, position) <= playerState.getStatSet().getRange()) {
                return true;
            }
        }
        return false;
    }

    private ArrayList<Position> getMovePosition(PlayerState playerState) {
        ArrayList<Position> positions = new ArrayList<>();
        for(int i = 0; i < Config.BOARD_SIZE; i++) {
            for(int j = 0; j < Config.BOARD_SIZE; j++) {
                if (Utility.manhattanDistance(playerState.getPosition(), new Position(1, j)) <= playerState.getStatSet().getSpeed()) {
                    positions.add(new Position(i, j));
                }
            }
        }
        return positions;
    }

    private int getClassCount(CharacterClass cs, GameState gameState, int playerIndex) {
        int count = 0;
        for (int i = 0; i < 4 && i != playerIndex; i++) {
            if (gameState.getPlayerStateByIndex(i).getCharacterClass() == cs) {
                count++;
            }
        }
        return count;
    }
}

