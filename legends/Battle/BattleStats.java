package legends.Battle;

import legends.Character.Hero;
import legends.Character.Monster;
import legends.Character.Spell;

// Observer #2: only counts things. Battle does not know it exists.
public class BattleStats extends BattleObserverAdapter {
    private int rounds = 0;
    private int monstersDefeated = 0;
    private int dodges = 0;
    private double damageDealt = 0;
    private double damageTaken = 0;

    public void roundStarted(int round) {
        rounds = round;
    }

    public void heroAttacked(Hero hero, Monster target, double damage) {
        damageDealt += damage;
    }

    public void spellCast(Hero hero, Spell spell, Monster target, double damage, String effectText) {
        damageDealt += damage;
    }

    public void attackDodged(String defenderName, String attackName) {
        dodges += 1;
    }

    public void monsterDefeated(Monster monster) {
        monstersDefeated += 1;
    }

    public void monsterAttacked(Monster monster, Hero target, double damage) {
        damageTaken += damage;
    }

    public String summary() {
        return "Battle summary: " + rounds + " round(s), " + Math.round(damageDealt) + " damage dealt, "
                + Math.round(damageTaken) + " damage taken, " + monstersDefeated
                + " monster(s) defeated, " + dodges + " dodge(s).";
    }
}
