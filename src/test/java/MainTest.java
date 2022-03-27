import StarterPack.action.AttackAction;
import StarterPack.networking.Server;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.concurrent.Callable;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MainTest {
    @Test
    public void testClientWorkFlow() {
        Callable<String> server =
                () -> {
                    Server turnServer = new Server(27, 1);
                    while (!turnServer.isOpen()) turnServer.open();
//                    turnServer.write("Hello.", 0);
                    AttackAction a = new ObjectMapper().readValue(turnServer.readAll().get(0), AttackAction.class);
                    assertEquals(a.getTarget(),
                            0);
                    System.out.println(a.getTarget());
                    turnServer.close();
                    return "Task's execution";
                };

    }
}
