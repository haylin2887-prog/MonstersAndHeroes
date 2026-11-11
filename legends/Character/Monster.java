package legends.Character;

import legends.Rules;

public abstract class Monster extends Entity {
    protected double baseDamage;
    protected double defense;
    protected double dodge;

    public Monster(String name, int level, double baseDamage, double defense, double dodge) {
        super(name, level);
        this.baseDamage = baseDamage;
        this.defense = defense;
        this.dodge = dodge;
    }
    
    public double attackDamage() {
        return baseDamage * Rules.MONSTER_DAMAGE_FACTOR;
    }
    
    @Override 
    public double dodgeChance() {
        return dodge * Rules.MONSTER_DODGE_FACTOR;
    }

    // Damage left after the monster's natural defense.
    public double afterDefense(double damage) {
        double result = damage - defense * Rules.DEFENSE_FACTOR;
        if (result < 0) 
            {
            result = 0;
        }
        return result;
    }

    // These three are used by the spell effects.
    public void reduceDamage(double percent) {
        baseDamage = baseDamage * (1 - percent);
    }

    public void reduceDefense(double percent) {
        defense = defense * (1 - percent);
    }

    public void reduceDodge(double percent) {
        dodge = dodge * (1 - percent);
    }

    public abstract String getKind();

    public String statusLine() {
        return name + " the " + getKind() + "  Lv" + level
                + "  HP " + Math.round(hp)
                + "  DMG " + Math.round(attackDamage())
                + "  DEF " + Math.round(defense)
                + "  DODGE " + Math.round(dodgeChance() * 100) + "%";
    }

    public String toString() {
        return statusLine();
    }

    // ---------- the three monster types (each is stronger in one stat) ----------
    public static class Dragon extends Monster {
        public Dragon(String name, int level, double damage, double defense, double dodge) {
            super(name, level, damage, defense, dodge);
            baseDamage = baseDamage * Rules.MONSTER_FAVORED_BONUS;
        }

        public String getKind() {
            return "Dragon";
        }
    }

    public static class Exoskeleton extends Monster {
        public Exoskeleton(String name, int level, double damage, double defense, double dodge) {
            super(name, level, damage, defense, dodge);
            this.defense = this.defense * Rules.MONSTER_FAVORED_BONUS;
        }

        public String getKind() {
            return "Exoskeleton";
        }
    }

    public static class Spirit extends Monster {
        public Spirit(String name, int level, double damage, double defense, double dodge) {
            super(name, level, damage, defense, dodge);
            this.dodge = this.dodge * Rules.MONSTER_FAVORED_BONUS;
        }

        
        
        public String getKind() {
            return "Spirit";
        }
    }
}
