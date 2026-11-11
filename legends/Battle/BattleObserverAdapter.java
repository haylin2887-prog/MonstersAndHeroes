package legends.Battle;

import legends.Character.Hero;
import legends.Character.Monster;
import legends.Character.Spell;

// Adapter pattern: the interface above has many methods, but most observers only
// care about a few. This class implements all of them with empty bodies, so a
// subclass only overrides the events it needs.
public abstract class BattleObserverAdapter implements BattleObserver {
    public void battleStarted() {
    }

    public void roundStarted(int round) {
    }

    public void heroAttacked(Hero hero, Monster target, double damage) {
    }

    public void spellCast(Hero hero, Spell spell, Monster target, double damage, String effectText) {
    }

    public void attackDodged(String defenderName, String attackName) {
    }

    public void monsterDefeated(Monster monster) {
    }

    public void monsterAttacked(Monster monster, Hero target, double damage) {
    }

    public void heroFainted(Hero hero) {
    }

    public void roundEnded() {
    }

    public void battleWon() {
    }

    public void battleLost() {
    }

    public void heroRewarded(Hero hero, int xp, int gold, boolean leveledUp) {
    }

    public void heroRevived(Hero hero) {
    }
}
