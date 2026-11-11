package legends;

import legends.Character.GameData;

public class Main {
    public static void main(String[] args) throws Exception {
        String folder = "data";
        if (args.length > 0) {
            folder = args[0];
        }
        GameData data = new GameData(folder);
        Game game = new Game(data);
        game.run();
    }
}
