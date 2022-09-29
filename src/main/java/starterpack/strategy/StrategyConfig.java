package starterpack.strategy;

public class StrategyConfig {

    /**
     * Return the strategy used by the bot given
     * @param playerIndex
     * @return
     */
    public static Strategy getStrategy(int playerIndex) {
        if (playerIndex == 0) {
            /*
            If I am player 0, use Strategy...?
             */
        }
        return new RandomStrategy();
    }
}
