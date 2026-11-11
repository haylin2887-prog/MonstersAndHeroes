package legends.Character;

import legends.Rules;

// Parent of Hero and Monster: both have a name, a level and HP.
public abstract class Entity {
    protected String name;
    protected int level;
    protected double hp;

    public Entity(String name, int level) {
        this.name = name;
        this.level = level;
        this.hp = level * Rules.HP_PER_LEVEL;
    }

    public String getName() {
        return name;
    }

    public int getLevel() {
        return level;
    }

    public double getHp() {
        return hp;
    }

    public boolean isFainted() {
        return hp <= 0;
    }

    public void takeDamage(double amount) {
        hp = hp - amount;
        if (hp < 0) {
            hp = 0;
        }
    }

    // Heroes and monsters calculate their dodge chance differently.
    public abstract double dodgeChance();

    public boolean tryDodge() {
        double chance = dodgeChance();
        if (chance > Rules.MAX_DODGE) {
            chance = Rules.MAX_DODGE;
        }
        return Rules.chance(chance);
    }
}
