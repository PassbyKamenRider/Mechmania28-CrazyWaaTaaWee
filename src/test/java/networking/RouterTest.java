//package networking;
//
//import StarterPack.GameState;
//import StarterPack.action.AttackAction;
//import StarterPack.networking.Client;
//import StarterPack.networking.Router;
//import StarterPack.networking.Server;
//import StarterPack.player.CharacterClass;
//import StarterPack.player.PlayerState;
//import StarterPack.player.Position;
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.Timeout;
//
//import java.util.Arrays;
//import java.util.List;
//import java.util.concurrent.Callable;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//import java.util.concurrent.TimeUnit;
//import java.util.concurrent.atomic.AtomicInteger;
//import java.util.stream.Collectors;
//import java.util.stream.Stream;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//
//public class RouterTest {
//    private void runAllCallables(List<Callable<String>> callables) {
//        ExecutorService executor = Executors.newFixedThreadPool(callables.size());
//        try {
//            executor.invokeAll(callables);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//
//        executor.shutdown();
//    }
//
//
//
//
//
////    @Test
////    @Timeout(value = 1000, unit = TimeUnit.MILLISECONDS)
////    public void testReceiveGameState() {
////        Server server = new Server(27, 1);
////        server.open();
////
////        // generate a gamestate
////        try {
////            server.write(new ObjectMapper().writeValueAsString(gameState), 0);
////        } catch (JsonProcessingException e) {
////            e.printStackTrace();
////        }
////
////        Router router = new Router(27);
////        assertTrue(router.isConnected());
////        GameState gameState = router.receiveGameState();
////        assertEquals(gameState, this.gameState);
////        router.cleanUp();
////        assertFalse(router.isConnected());
////
////        server.close();
////        assertFalse(server.isOpen());
////
////
////    }
//
//    @Test
//    @Timeout(value = 1000, unit = TimeUnit.MILLISECONDS)
//    public void testReceiveGameState() {
//        AtomicInteger index = new AtomicInteger(0);
//        List<PlayerState> playerStateList =
//                Stream.generate(() ->
//                        new PlayerState(CharacterClass.KNIGHT, new Position(0, index.getAndIncrement()))
//                ).limit(4).collect(Collectors.toList());
//        GameState gameState = new GameState(playerStateList);
//
//        Callable<String> server =
//                () -> {
//                    Server turnServer = new Server(27, 1);
//                    while (!turnServer.isOpen()) turnServer.open();
//                    try {
//                        turnServer.write(new ObjectMapper().writeValueAsString(gameState), 0);
//                    } catch (JsonProcessingException e) {
//                        e.printStackTrace();
//                    }
//                    turnServer.close();
//                    return "Task's execution";
//                };
//
//        Callable<String> client =
//                () -> {
//                    Router router = new Router(27);
//                    router.connect();
//                    assertEquals(router.receiveGameState().getPlayerStateList().get(0),
//                            new Position(0, 0));
//                    return "Task's execution";
//                };
//
//        runAllCallables(Arrays.asList(server, client));
//    }
//
//    @Test
//    @Timeout(value = 500, unit = TimeUnit.MILLISECONDS)
//    public void testWriteStringReverse() {
//        Callable<String> server =
//                () -> {
//                    Server turnServer = new Server(27, 1);
//                    while (!turnServer.isOpen()) turnServer.open();
////                    turnServer.write("Hello.", 0);
//                    AttackAction a = new ObjectMapper().readValue(turnServer.readAll().get(0), AttackAction.class);
//                    assertEquals(a.getTarget(),
//                            0);
//                    System.out.println(a.getTarget());
//                    turnServer.close();
//                    return "Task's execution";
//                };
//
//
//        Callable<String> client =
//                () -> {
//                    Client testClient = new Client(27);
//                    while (!testClient.isConnected()) testClient.connect();
//                    testClient.write(new ObjectMapper().writeValueAsString(
//                            new AttackAction(0, 0)
//                    ));
//                    testClient.disconnect();
//                    return "Task's execution";
//                };
//
//        runAllCallables(Arrays.asList(server, client));
//    }
//
//    @Test
//    public void testJson() {
//        ObjectMapper objectMapper = new ObjectMapper();
//        String s = "";
//        try {
//            s = objectMapper.writeValueAsString(new AttackAction(0, 0));
//        } catch (JsonProcessingException e) {e.printStackTrace();}
//        AttackAction a;
//        try {
//             a = objectMapper.readValue(s, AttackAction.class);
//            System.out.println(a.getTarget());
//        } catch (Exception e) {e.printStackTrace();}
//
//    }
//
//
//}
