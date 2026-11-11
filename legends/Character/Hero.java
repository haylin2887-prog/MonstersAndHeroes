package legends.Character;

import java.util.ArrayList;

import legends.Rules;

public abstract class Hero extends Entity {
    private double mp;
    private double strength;
    private double dexterity;
    private double agility;
    private int gold;
    private int xp;

    // Which skills this type of hero is good at.
    private boolean favorsStrength;
    private boolean favorsDexterity;
    private boolean favorsAgility;

    private ArrayList<Item> inventory = new ArrayList<Item>();
    private Weapon weapon;      // equipped weapon (null if none)
    private boolean bothHands;  // is the weapon held with both hands?
    private Armor armor;        // equipped armor (null if none)

    public Hero(String name, double mp, double str, double agi, double dex, int gold, int xp,
                boolean favorsStrength, boolean favorsDexterity, boolean favorsAgility) {
        super(name, 1);
        this.mp = mp;
        this.gold = gold;
        this.xp = xp;
        this.favorsStrength = favorsStrength;
        this.favorsDexterity = favorsDexterity;
        this.favorsAgility = favorsAgility;

        // favored skills start a little higher
        double bonus = 1 + Rules.FAVORED_START_BONUS;
        this.strength = str;
        if (favorsStrength) {
            this.strength = str * bonus;
        }
        this.agility = agi;
        if (favorsAgility) {
            this.agility = agi * bonus;
        }
        this.dexterity = dex;
        if (favorsDexterity) {
            this.dexterity = dex * bonus;
        }
    }

    public abstract String heroType();

    // ---------- getters ----------
    public double getMp() {
        return mp;
    }

    public double getDexterity() {
        return dexterity;
    }

    public int getGold() {
        return gold;
    }

    public ArrayList<Item> getInventory() {
        return inventory;
    }

    public double dodgeChance() {
        return agility * Rules.HERO_DODGE_FACTOR;
    }

    // ---------- money and items ----------
    public boolean canAfford(int cost) {
        return gold >= cost;
    }

    public void spend(int amount) {
        gold -= amount;
    }

    public void earn(int amount) {
        gold += amount;
    }

    public void addItem(Item item) {
        inventory.add(item);
    }

    public void removeItem(Item item) {
        inventory.remove(item);
        if (item == weapon) {
            weapon = null;
        }
        if (item == armor) {
            armor = null;
        }
    }

    // Generic method: one method for every kind of item.
   
    public <T extends Item> ArrayList<T> getItems(Class<T> type) {
        ArrayList<T> list = new ArrayList<T>();
        for (Item item : inventory) {
            if (type.isInstance(item)) {
                list.add(type.cast(item));
            }
        }
        return list;
    }

    // ---------- equipment ----------
    public void equipWeapon(Weapon newWeapon, boolean useBothHands) {
        weapon = newWeapon;
        if (newWeapon.getHands() == 2) {
            bothHands = true;
        } else {
            bothHands = useBothHands;
        }
    }

    public void equipArmor(Armor newArmor) {
        armor = newArmor;
    }

    // ---------- fighting ----------
    public double attackDamage() {
        double weaponDamage = 0;
        if (weapon != null) {
            weaponDamage = weapon.getDamage();
            if (bothHands && weapon.getHands() == 1) {
                weaponDamage = weaponDamage * Rules.TWO_HAND_BONUS;
            }
        }
        return (strength + weaponDamage) * Rules.ATTACK_FACTOR;
    }

    // Call after every attack: weapons wear out.
    public void weaponUsed() {
        if (weapon == null) {
            return;
        }
        weapon.useOnce();
        if (weapon.isBroken()) {
            removeItem(weapon);
        }
    }

    // Armor lowers the damage; returns the damage the hero really took.
    public double receiveDamage(double damage) {
        double taken = damage;
        if (armor != null) {
            taken = damage - armor.blockAmount();
            if (taken < 0) {
                taken = 0;
            }
            armor.useOnce();
            if (armor.isBroken()) {
                removeItem(armor);
            }
        }
        takeDamage(taken);
        return taken;
    }

    public boolean spendMana(double cost) {
        if (mp < cost) {
            return false;
        }
        mp -= cost;
        return true;
    }

    // End of a battle round: living heroes get 10% back.
    public void regenerate() {
        if (isFainted()) {
            return;
        }
        hp = hp * Rules.REGEN;
        mp = mp * Rules.REGEN;
    }

    // After a won battle fainted heroes come back with half HP and MP.
    public void revive() {
        hp = level * Rules.HP_PER_LEVEL / 2.0;
        mp = mp / 2;
    }

    // Used by potions. The stat is given by name.
    public void addToStat(String stat, double amount) {
        switch (stat) {
            case "health":
                hp += amount;
                break;
            case "mana":
                mp += amount;
                break;
            case "strength":
                strength += amount;
                break;
            case "dexterity":
                dexterity += amount;
                break;
            case "agility":
                agility += amount;
                break;
            default:
                break;
        }
    }

    // ---------- experience and levels ----------
    // Returns true if the hero reached a new level.
    public boolean gainExperience(int amount) {
        xp += amount;
        boolean leveledUp = false;
        while (xp >= level * Rules.XP_PER_LEVEL) {
            levelUp();
            leveledUp = true;
        }
        return leveledUp;
    }

    private void levelUp() {
        level += 1;
        strength = strength * (1 + skillGain(favorsStrength));
        dexterity = dexterity * (1 + skillGain(favorsDexterity));
        agility = agility * (1 + skillGain(favorsAgility));
        hp = level * Rules.HP_PER_LEVEL;
        mp = mp * Rules.MP_LEVEL_UP;
    }

    // Every skill grows 5%, favored skills grow 5% more.
    private double skillGain(boolean favored) {
        double gain = Rules.LEVEL_UP_SKILL_GAIN;
        if (favored) {
            gain += Rules.FAVORED_EXTRA_GAIN;
        }
        return gain;
    }

    // ---------- text shown to the player ----------
    public String statusLine(boolean inBattle) {
        String weaponText = "none";
        if (weapon != null) {
            weaponText = weapon.getName();
            if (bothHands) {
                weaponText = weaponText + " (2 hands)";
            }
        }
        String armorText = "none";
        if (armor != null) {
            armorText = armor.getName();
        }
        String equipment = "weapon: " + weaponText + ", armor: " + armorText;

        String text = name + " the " + heroType() + "  Lv" + level
                + "  HP " + Math.round(hp) + "  MP " + Math.round(mp);
        if (inBattle) {
            if (isFainted()) {
                text = text + "  [FAINTED]";
            }
            return text + "  " + equipment;
        }
        return text + "  XP " + xp + "  Gold " + gold
                + "  STR " + Math.round(strength) + " DEX " + Math.round(dexterity)
                + " AGI " + Math.round(agility) + "  " + equipment;
    }

    // ---------- the three hero types with different favored skills: strength, dexterity, agility) ----------
    public static class Warrior extends Hero {
        public Warrior(String name, double mp, double str, double agi, double dex, int gold, int xp) {
            super(name, mp, str, agi, dex, gold, xp, true, false, true);
        }

        public String heroType() {
            return "Warrior";
        }
    }

    public static class Sorcerer extends Hero {
        public Sorcerer(String name, double mp, double str, double agi, double dex, int gold, int xp) {
            super(name, mp, str, agi, dex, gold, xp, false, true, true);
        }

        public String heroType() {
            return "Sorcerer";
        }
    }

    public static class Paladin extends Hero {
        public Paladin(String name, double mp, double str, double agi, double dex, int gold, int xp) {
            super(name, mp, str, agi, dex, gold, xp, true, true, false);
        }

        public String heroType() {
            return "Paladin";
        }
    }
}
