package starterpack.strategy;

public class StrategyConfig {
    public static Strategy getStrategy(int playerIndex) {
        return new RandomStrategy();
    }
}
