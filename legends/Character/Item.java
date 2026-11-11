package legends.Character;

import java.util.ArrayList;

import legends.Rules;

// Parent of everything that can be bought, sold and carried.
public abstract class Item {
    protected String name;
    protected int price;
    protected int level;
    protected int usesLeft;

    public Item(String name, int price, int level, int uses) {
        this.name = name;
        this.price = price;
        this.level = level;
        this.usesLeft = uses;
    }

    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }

    public int getLevel() {
        return level;
    }

    public int sellPrice() {
        return price / 2;
    }

    public boolean isBroken() {
        return usesLeft <= 0;
    }

    public void useOnce() {
        usesLeft = usesLeft - 1;
    }

    // A fresh copy with full uses, so two markets never share the same object.
    public abstract Item copy();

    // The text shown after the name in menus.
    public abstract String describe();

    public String toString() {
        return String.format("%-22s Lv%-2d %5dg  %s", name, level, price, describe());
    }
}

// A potion can be used once and raises one or more stats.
class Potion extends Item {
    private int amount;
    private ArrayList<String> stats;   // "health", "mana", "strength", "dexterity", "agility"

    public Potion(String name, int price, int level, int amount, ArrayList<String> stats) {
        super(name, price, level, 1);
        this.amount = amount;
        this.stats = stats;
    }

    public String use(Hero hero) {
        for (String stat : stats) {
            hero.addToStat(stat, amount);
        }
        useOnce();
        return hero.getName() + " used " + name + " (+" + amount + " " + statNames() + ")!";
    }

    private String statNames() {
        String text = "";
        for (int i = 0; i < stats.size(); i++) {
            if (i > 0) {
                text = text + "/";
            }
            text = text + stats.get(i);
        }
        return text;
    }

    public Item copy() {
        return new Potion(name, price, level, amount, stats);
    }

    public String describe() {
        return "Potion  +" + amount + " " + statNames();
    }
}

class Armor extends Item {
    private int reduction;

    public Armor(String name, int price, int level, int reduction) {
        super(name, price, level, Rules.ARMOR_USES);
        this.reduction = reduction;
    }

    // How much damage this armor blocks from one hit.
    public double blockAmount() {
        return reduction * Rules.ARMOR_FACTOR;
    }

    public Item copy() {
        return new Armor(name, price, level, reduction);
    }

    public String describe() {
        return "Armor   reduces damage by " + reduction + ", uses " + usesLeft;
    }
}

class Weapon extends Item {
    private int damage;
    private int hands;

    public Weapon(String name, int price, int level, int damage, int hands) {
        super(name, price, level, Rules.WEAPON_USES);
        this.damage = damage;
        this.hands = hands;
    }

    public int getDamage() {
        return damage;
    }

    public int getHands() {
        return hands;
    }

    public Item copy() {
        return new Weapon(name, price, level, damage, hands);
    }

    public String describe() {
        return "Weapon  damage " + damage + ", " + hands + "-handed, uses " + usesLeft;
    }
}

