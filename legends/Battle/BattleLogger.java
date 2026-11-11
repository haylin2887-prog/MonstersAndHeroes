package legends.Battle;

import legends.Console;
import legends.Character.Hero;
import legends.Character.Monster;
import legends.Character.Spell;

// Observer #1: prints what happened ("who did what") for the player.
public class BattleLogger extends BattleObserverAdapter {
    private Console console;

    public BattleLogger(Console console) {
        this.console = console;
    }

    public void battleStarted() {
        console.println();
        console.println("*** A BATTLE BEGINS! ***");
    }

    public void roundStarted(int round) {
        console.println();
        console.println("--- Round " + round + ": Heroes' turn ---");
    }

    public void heroAttacked(Hero hero, Monster target, double damage) {
        console.println(hero.getName() + " attacked " + target.getName() + " for " + Math.round(damage) + " damage!");
    }

    public void spellCast(Hero hero, Spell spell, Monster target, double damage, String effectText) {
        console.println(hero.getName() + " cast " + spell.getName() + " on " + target.getName()
                + " for " + Math.round(damage) + " damage! " + effectText);
    }

    public void attackDodged(String defenderName, String attackName) {
        console.println(defenderName + " dodged " + attackName + "!");
    }

    public void monsterDefeated(Monster monster) {
        console.println(monster.getName() + " was defeated!");
    }

    public void monsterAttacked(Monster monster, Hero target, double damage) {
        console.println(monster.getName() + " attacked " + target.getName() + " for " + Math.round(damage) + " damage!");
    }

    public void heroFainted(Hero hero) {
        console.println(hero.getName() + " fainted!");
    }

    public void roundEnded() {
        console.println();
        console.println("Surviving heroes recover some HP and MP.");
    }

    public void battleWon() {
        console.println();
        console.println("*** VICTORY! ***");
    }

    public void battleLost() {
        console.println();
        console.println("All heroes have fainted...");
    }

    public void heroRewarded(Hero hero, int xp, int gold, boolean leveledUp) {
        String text = hero.getName() + " earned " + xp + " XP and " + gold + " gold.";
        if (leveledUp) {
            text = text + "  LEVEL UP! Now level " + hero.getLevel() + "!";
        }
        console.println(text);
    }

    public void heroRevived(Hero hero) {
        console.println(hero.getName() + " was revived with half HP and MP (no reward).");
    }
}
