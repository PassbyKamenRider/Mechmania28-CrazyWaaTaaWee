package starterpack.strategy;

public class StrategiesForEachBot {
    public static Strategy strategyAsBot0 = new InfiniteLoopStrategy();
    public static Strategy strategyAsBot1 = new CrashingStrategy();
    public static Strategy strategyAsBot2 = new NullStrategy();
    public static Strategy strategyAsBot3 = new RandomStrategy();
}
