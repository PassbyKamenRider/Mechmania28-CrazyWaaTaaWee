package networking;

import starterpack.networking.Client;
import starterpack.networking.Server;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class ServerClientTest {
  private static void runAllCallables(List<Callable<String>> callables) {
    ExecutorService executor = Executors.newFixedThreadPool(callables.size());
    try {
      executor.invokeAll(callables);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    executor.shutdown();
  }

  @Test
  @Timeout(value = 500, unit = TimeUnit.MILLISECONDS)
  public void testLifeCycle() {
    Callable<String> server =
        () -> {
          Server turnServer = new Server(27, 1);
          while (!turnServer.isOpen()) turnServer.open();
          turnServer.close();
          assertFalse(turnServer.isOpen());
          return "Task's execution";
        };

    Callable<String> client =
        () -> {
          Client testClient = new Client(27);
          while (!testClient.isConnected()) testClient.connect();
          testClient.disconnect();
          assertFalse(testClient.isConnected());
          return "Task's execution";
        };

    runAllCallables(Arrays.asList(server, client));
  }

  @Test
  @Timeout(value = 500, unit = TimeUnit.MILLISECONDS)
  public void testWriteString() {
    Callable<String> server =
        () -> {
          Server turnServer = new Server(27, 1);
          while (!turnServer.isOpen()) turnServer.open();
          turnServer.write("Hello.", 0);
          turnServer.close();
          return "Task's execution";
        };

    Callable<String> client =
        () -> {
          Client testClient = new Client(27);
          while (!testClient.isConnected()) testClient.connect();
          assertEquals(testClient.read(), "Hello.");
          testClient.disconnect();
          return "Task's execution";
        };

    runAllCallables(Arrays.asList(server, client));
  }

    @Test
    @Timeout(value = 500, unit = TimeUnit.MILLISECONDS)
    public void testWriteString0() {
        Callable<String> server =
                () -> {
                    Server turnServer = new Server(27, 1);
                    while (!turnServer.isOpen()) turnServer.open();
                    turnServer.write0("Hello.");
                    turnServer.close();
                    return "Task's execution";
                };

        Callable<String> client =
                () -> {
                    Client testClient = new Client(27);
                    while (!testClient.isConnected()) testClient.connect();
                    assertEquals(testClient.read(), "Hello.");
                    testClient.disconnect();
                    return "Task's execution";
                };

        runAllCallables(Arrays.asList(server, client));
    }

    @Test
    @Timeout(value = 500, unit = TimeUnit.MILLISECONDS)
    public void testWriteStringReverse() {
        Callable<String> server =
                () -> {
                    Server turnServer = new Server(27, 1);
                    while (!turnServer.isOpen()) turnServer.open();
//                    turnServer.write("Hello.", 0);
                    System.out.println(turnServer.readAll());
                    turnServer.close();
                    return "Task's execution";
                };

        Callable<String> client =
                () -> {
                    Client testClient = new Client(27);
                    while (!testClient.isConnected()) testClient.connect();
//                    assertEquals(testClient.read(), "Hello.");
                    testClient.write("abcdefg");
                    testClient.disconnect();
                    return "Task's execution";
                };

        runAllCallables(Arrays.asList(server, client));
    }

    @Test
    @Timeout(value = 500, unit = TimeUnit.MILLISECONDS)
    public void testTwoSide() {
        Callable<String> server =
                () -> {
                    Server turnServer = new Server(27222, 1);
                    while (!turnServer.isOpen()) turnServer.open();
                    turnServer.write("wake", 0);
                    turnServer.writeAll(1);

                    System.out.println(turnServer.readAll());
                    turnServer.close();
                    return "Task's execution";
                };

        Callable<String> client =
                () -> {
                    Client testClient = new Client(27222);
                    while (!testClient.isConnected()) testClient.connect();
                    System.out.println(testClient.read());
                    System.out.println(testClient.read());

                    testClient.write("abcdefg");
                    testClient.disconnect();
                    return "Task's execution";
                };

        runAllCallables(Arrays.asList(server, client));
    }

    public static void main(String[] args) {
        Callable<String> server =
                () -> {
                    Server turnServer = new Server(27000, 1);
                    while (!turnServer.isOpen()) turnServer.open();
                    turnServer.write("Hello.", 0);
                    turnServer.write("0", 0);


                    System.out.println("Server: " + turnServer.readAll());
                    turnServer.close();
                    return "Task's execution";
                };

        Callable<String> client =
                () -> {
                    Client testClient = new Client(27000);
                    while (!testClient.isConnected()) testClient.connect();
                    System.out.println("Client:" + testClient.read());
                    System.out.println("Client: " + testClient.read());

                    testClient.write("KNIGHT");

                    testClient.disconnect();
                    return "Task's execution";
                };

        runAllCallables(Arrays.asList(server, client));
    }
}
