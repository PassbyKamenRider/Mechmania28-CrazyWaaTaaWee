import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainTest {

    private void runAllCallables(List<Callable<String>> callables) {
        ExecutorService executor = Executors.newFixedThreadPool(callables.size());
        try {
            executor.invokeAll(callables);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        executor.shutdown();
    }

//    @Test
//    public void testClientWorkFlow() {
//        Callable<String> s =
//                () -> {
//                    Server server = new Server(27, 1);
//                    while (!server.isOpen()) server.open();
////                    server.write("Hello.", 0);
//                    AttackAction a = new ObjectMapper().readValue(server.readAll().get(0), AttackAction.class);
//                    assertEquals(a.getTarget(),
//                            0);
//                    System.out.println(a.getTarget());
//                    server.close();
//                    return "Task's execution";
//                };

//        Callable<String> client =
//                () -> {
//                    Main.main(new String[]{"27"});
//                    return "Task's execution";
//                };
//
//        runAllCallables(Arrays.asList(s, client));

    }
