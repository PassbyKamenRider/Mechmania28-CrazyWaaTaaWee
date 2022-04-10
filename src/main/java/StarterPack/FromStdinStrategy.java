package StarterPack;

import StarterPack.player.CharacterClass;
import StarterPack.player.Item;
import StarterPack.player.Position;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class FromStdinStrategy implements Strategy {
    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
    public CharacterClass initialClass() {
        String s = "KNIGHT";
        try {
            s = bufferedReader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (s.equals("WIZARD"))
            return CharacterClass.WIZARD;
        if (s.equals("ARCHER"))
            return CharacterClass.ARCHER;
        return CharacterClass.KNIGHT;
    }

    public Position moveActionDecision(GameState gameState, int myPlayerIndex) {
        String s = "0";
        Position position = new Position(0, 0);
        try {
            s = bufferedReader.readLine();
            position.setX(Integer.parseInt(s));
            s = bufferedReader.readLine();
            position.setY(Integer.parseInt(s));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return position;

    }

    public int attackActionDecision(GameState gameState, int myPlayerIndex) {
        String s = "0";
        try {
            s = bufferedReader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Integer.parseInt(s);
    }

    public Item buyActionDecision(GameState gameState, int myPlayerIndex) {
        String s = "NONE";
        try {
            s = bufferedReader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (Item item : Item.values()) {
            if (item.name().equals(s)) {
                return item;
            }
        }
        return Item.NONE;
    }

    public boolean useActionDecision(GameState gameState, int myPlayerIndex) {
        String s = "NONE";
        try {
            s = bufferedReader.readLine();
            return Boolean.parseBoolean(s);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}
