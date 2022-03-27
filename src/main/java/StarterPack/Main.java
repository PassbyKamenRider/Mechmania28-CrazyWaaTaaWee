package StarterPack;

import StarterPack.networking.Router;

public class Main {
    public static void main(String[] args) {
        // Establish connection, send Ping message
        Router router = new Router(Integer.parseInt(args[0]));
        Strategy strategy = new Strategy();
        router.pingEngine(strategy.initialClass());

        while (true) {

            String gameStateString = router.read();

            if (gameStateString.equals("fin")) {
                break;
            }

            GameState gameState = router.parseMessageAsGameState(gameStateString);
            router.sendUseAction(strategy.useActionDecision(gameState, router.getPlayerNumber()));

            gameState = router.receiveGameState();
            router.sendMoveAction(strategy.moveActionDecision(gameState, router.getPlayerNumber()));

            gameState = router.receiveGameState();
            router.sendAttackAction(strategy.attackActionDecision(gameState, router.getPlayerNumber()));

            gameState = router.receiveGameState();
            router.sendBuyAction(strategy.buyActionDecision(gameState, router.getPlayerNumber()));

        }

    }
}
