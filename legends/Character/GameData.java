package legends.Character;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import legends.Rules;
import legends.Market.Market;

// Reads the text files once. The raw rows are kept, and a brand-new Hero or Monster
// is built from a row every time one is needed, so every new game starts with the
// default values from the files.
public class GameData {
    private ArrayList<String[]> warriorRows;
    private ArrayList<String[]> sorcererRows;
    private ArrayList<String[]> paladinRows;
    private ArrayList<String[]> dragonRows;
    private ArrayList<String[]> exoskeletonRows;
    private ArrayList<String[]> spiritRows;
    private ArrayList<Item> catalog = new ArrayList<Item>();   // everything markets can sell

    public GameData(String folder) throws IOException {
        warriorRows = readRows(folder + "/Warriors.txt");
        sorcererRows = readRows(folder + "/Sorcerers.txt");
        paladinRows = readRows(folder + "/Paladins.txt");
        dragonRows = readRows(folder + "/Dragons.txt");
        exoskeletonRows = readRows(folder + "/Exoskeletons.txt");
        spiritRows = readRows(folder + "/Spirits.txt");

        for (String[] row : readRows(folder + "/Weaponry.txt")) {
            catalog.add(new Weapon(name(row), number(row, 1), number(row, 2), number(row, 3), number(row, 4)));
        }
        for (String[] row : readRows(folder + "/Armory.txt")) {
            catalog.add(new Armor(name(row), number(row, 1), number(row, 2), number(row, 3)));
        }
        for (String[] row : readRows(folder + "/Potions.txt")) {
            catalog.add(makePotion(row));
        }
        addSpells(folder + "/IceSpells.txt", new IceEffect());
        addSpells(folder + "/FireSpells.txt", new FireEffect());
        addSpells(folder + "/LightningSpells.txt", new LightningEffect());
    }

    // ---------- reading files ----------
    // Each line becomes an array of words. Empty lines and the header line are skipped.
    private ArrayList<String[]> readRows(String path) throws IOException {
        ArrayList<String[]> rows = new ArrayList<String[]>();
        List<String> lines = Files.readAllLines(Paths.get(path));
        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty() || line.toLowerCase().startsWith("name")) {
                continue;
            }
            rows.add(line.split("\\s+"));
        }
        return rows;
    }

    // The files use underscores in names: Gaerdal_Ironhand -> Gaerdal Ironhand
    private String name(String[] row) {
        return row[0].replace('_', ' ');
    }

    private int number(String[] row, int index) {
        return (int) Double.parseDouble(row[index]);
    }

    private double decimal(String[] row, int index) {
        return Double.parseDouble(row[index]);
    }

    private void addSpells(String path, SpellEffect effect) throws IOException {
        for (String[] row : readRows(path)) {
            catalog.add(new Spell(name(row), number(row, 1), number(row, 2), number(row, 3), number(row, 4), effect));
        }
    }

    // Potion rows end with the stat names, for example "Health/Mana/Strength"
    // or "All Health/Mana/Dexterity/Defense/Agility".
    private Potion makePotion(String[] row) {
        String stats = "";
        for (int i = 4; i < row.length; i++) {
            stats += row[i] + " ";
        }
        stats = stats.trim();
        if (stats.toLowerCase().startsWith("all ")) {
            stats = stats.substring(4);
        }
        ArrayList<String> statList = new ArrayList<String>();
        String[] parts = stats.split("/");
        for (String part : parts) {
            String stat = part.trim().toLowerCase();
            if (!stat.equals("defense")) {   // heroes have no defense stat
                statList.add(stat);
            }
        }
        return new Potion(name(row), number(row, 1), number(row, 2), number(row, 3), statList);
    }

    // ---------- heroes ----------
    // Brand-new hero objects (every row of every hero file).
    public ArrayList<Hero> createAllHeroes() {
        ArrayList<Hero> heroes = new ArrayList<Hero>();
        for (String[] row : warriorRows) {
            heroes.add(new Hero.Warrior(name(row), decimal(row, 1), decimal(row, 2), decimal(row, 3),
                    decimal(row, 4), number(row, 5), number(row, 6)));
        }
        for (String[] row : sorcererRows) {
            heroes.add(new Hero.Sorcerer(name(row), decimal(row, 1), decimal(row, 2), decimal(row, 3),
                    decimal(row, 4), number(row, 5), number(row, 6)));
        }
        for (String[] row : paladinRows) {
            heroes.add(new Hero.Paladin(name(row), decimal(row, 1), decimal(row, 2), decimal(row, 3),
                    decimal(row, 4), number(row, 5), number(row, 6)));
        }
        return heroes;
    }

    // ---------- monsters ----------
    // Picks a random monster from the files and scales it to the given level.
    public Monster createMonster(int level) {
        int kind = Rules.randomInt(3);
        String[] row;
        if (kind == 0) {
            
            row = dragonRows.get(Rules.randomInt(dragonRows.size()));
        } else if (kind == 1) {
            row = exoskeletonRows.get(Rules.randomInt(exoskeletonRows.size()));
        } else {
            row = spiritRows.get(Rules.randomInt(spiritRows.size()));
        }

        double fileLevel = decimal(row, 1);
        if (fileLevel < 1) {
            fileLevel = 1;
        }
        double scale = level / fileLevel;
        double dodgeScale = scale;
        if (dodgeScale > 3) {
            dodgeScale = 3;
        }
        double damage = decimal(row, 2) * scale;
        double defense = decimal(row, 3) * scale;
        double dodge = decimal(row, 4) * dodgeScale;

        if (kind == 0) {
            
            return new Monster.Dragon(name(row), level, damage, defense, dodge);
        } else if (kind == 1) {
            
            return new Monster.Exoskeleton(name(row), level, damage, defense, dodge);
        }
        return new Monster.Spirit(name(row), level, damage, defense, dodge);
    }

    // ---------- markets ----------
    // Every market gets its own random half of the catalog.
    public Market createMarket() {
        ArrayList<Item> items = new ArrayList<Item>();
        for (Item item : catalog) {
            if (Rules.chance(0.5)) {
                
                items.add(item.copy());
            }
        }
        if (items.isEmpty()) {
            items.add(catalog.get(0).copy());
        }
        return new Market(items);
    }
}
