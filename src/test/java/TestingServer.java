import StarterPack.GameState;
import StarterPack.networking.Server;
import StarterPack.player.CharacterClass;
import StarterPack.player.PlayerState;
import StarterPack.player.Position;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TestingServer {
    public static void main(String[] args) {
        Server server = new Server(27, 1);
        while (!server.isOpen()) server.open();
        server.write("wake", 0);
        server.write("0", 0);


        System.out.println("Server: " + server.readAll());

        List<PlayerState> playerStateList =
                Stream.generate(() ->
                        new PlayerState(CharacterClass.KNIGHT, new Position(0, 0))
                ).limit(4).collect(Collectors.toList());
        GameState gameState = new GameState(playerStateList);
        for (int i = 0; i < 3 * 4; i++) {
            server.writeAll(gameState);
            System.out.println(server.readAll());
        }
        server.write("fin", 0);

        server.close();


    }
}
