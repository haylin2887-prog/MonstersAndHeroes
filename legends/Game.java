package legends;

import java.util.ArrayList;

import legends.Battle.Battle;
import legends.Battle.BattleLogger;
import legends.Battle.BattleStats;
import legends.Character.GameData;
import legends.Character.Hero;
import legends.Character.Menus;
import legends.Character.Monster;
import legends.Character.Party;
import legends.Market.Market;
import legends.World.Tile;
import legends.World.World;

// The main controller: sets up the party and runs the walking-around loop.
public class Game {
    private GameData data;
    private Console console = new Console();
    private Menus menus = new Menus(console);
    private Party party = new Party();
    private World world;
    private boolean gameOver = false;

    public Game(GameData data) {
        this.data = data;
    }

    public void run() {
        printHelp();
        chooseParty();
        world = new World(Rules.WORLD_SIZE, data);
        int[] start = world.findCommonTile();
        party.moveTo(start[0], start[1]);
        explore();
        console.println("Thanks for playing Monsters and Heroes!");
    }

    private void printHelp() {
        console.println("==============================================");
        console.println("        LEGENDS: MONSTERS AND HEROES");
        console.println("==============================================");
        console.println("Lead a party of heroes across the land. Fight monsters to earn gold and");
        console.println("experience, then spend gold in markets on weapons, armor, potions and spells.");
        console.println();
        console.println("CONTROLS (outside battle):");
        console.println("  W/A/S/D  move up/left/down/right     M  enter market (when standing on M)");
        console.println("  I  show hero information             E  equip items / drink potions");
        console.println("  H  show this help again              Q  quit (works at any prompt)");
        console.println("In battle, type the number shown next to an action. Heroes win by defeating all");
        console.println("monsters; if every hero faints, the game is over. Walking on blank tiles may");
        console.println("start a battle. Fainted heroes are revived (at half HP/MP) after a victory.");
        console.println();
    }

    private void chooseParty() {
        int count = console.readInt("How many heroes (" + Rules.MIN_PARTY + "-" + Rules.MAX_PARTY + ")? ",
                Rules.MIN_PARTY, Rules.MAX_PARTY);
        ArrayList<Hero> available = data.createAllHeroes();
        console.println();
        console.println("Available heroes:");
        for (int i = 0; i < available.size(); i++) {
            console.println("  " + (i + 1) + ") " + available.get(i).statusLine(false));
        }
        for (int i = 1; i <= count; i++) {
            int choice = console.readInt("Pick hero #" + i + ": ", 1, available.size());
            party.addHero(available.get(choice - 1));
        }
    }

    private void explore() {
        while (!gameOver) {
            console.println();
            console.println(world.render(party));
            console.println("[W/A/S/D] move  [I] info  [E] equip/potions  [M] market  [H] help  [Q] quit");
            String command = console.readLine("> ").toLowerCase();

            switch (command) {
                case "w":
                    move(-1, 0);
                    break;
                case "s":
                    move(1, 0);
                    break;
                case "a":
                    move(0, -1);
                    break;
                case "d":
                    move(0, 1);
                    break;
                case "i":
                    for (Hero hero : party.getHeroes()) {
                        console.println(hero.statusLine(false));
                    }
                    break;
                case "e":
                    manageInventory();
                    break;
                case "m":
                    enterMarket();
                    break;
                case "h":
                    printHelp();
                    break;
                default:
                    console.println("Unknown command. Press H for help.");
                    break;
            }
        }
    }

    private void move(int rowChange, int colChange) {
        int row = party.getRow() + rowChange;
        int col = party.getCol() + colChange;
        if (!world.inBounds(row, col)) {
            console.println("You can't leave the world!");
            return;
        }
        Tile tile = world.getTile(row, col);
        if (!tile.isAccessible()) {
            console.println("That way is blocked.");
            return;
        }
        party.moveTo(row, col);
        if (tile.getMarket() != null) {
            console.println("You arrived at a market. Press M to shop.");
        }
        if (tile.canTriggerBattle() && Rules.chance(Rules.BATTLE_CHANCE)) {
            startBattle();
        }
    }

    private void startBattle() {
        // one monster per hero, level = highest hero level
        ArrayList<Monster> monsters = new ArrayList<Monster>();
        for (int i = 0; i < party.size(); i++) {
            monsters.add(data.createMonster(party.highestLevel()));
        }

        Battle battle = new Battle(party, monsters, console, menus);
        BattleStats stats = new BattleStats();
        battle.addObserver(new BattleLogger(console));   // prints the events
        battle.addObserver(stats);                       // counts the events

        boolean won = battle.run();
        console.println(stats.summary());
        if (!won) {
            console.println("GAME OVER.");
            gameOver = true;
        }
    }

    private void enterMarket() {
        Market market = world.getTile(party.getRow(), party.getCol()).getMarket();
        if (market == null) {
            console.println("There is no market here. Look for an 'M' tile.");
        } else {
            menus.visitMarket(market, party);
        }
    }

    private void manageInventory() {
        ArrayList<Hero> heroes = party.getHeroes();
        for (int i = 0; i < heroes.size(); i++) {
            console.println("  " + (i + 1) + ") " + heroes.get(i).getName());
        }
        int choice = console.readInt("Which hero? (0 to cancel) > ", 0, heroes.size());
        if (choice == 0) {
            return;
        }
        Hero hero = heroes.get(choice - 1);
        console.println("  1) Equip weapon/armor  2) Use potion");
        int action = console.readInt("> ", 1, 2);
        if (action == 1) {
            menus.equip(hero);
        } else {
            menus.usePotion(hero);
        }
    }
}
