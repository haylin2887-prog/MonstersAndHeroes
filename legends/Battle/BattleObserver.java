package legends.Battle;

import legends.Character.Hero;
import legends.Character.Monster;
import legends.Character.Spell;

// Observer pattern: Battle tells every observer what happens.
// The observers decide what to do with the news (print it, count it).
public interface BattleObserver {
    void battleStarted();

    void roundStarted(int round);

    void heroAttacked(Hero hero, Monster target, double damage);

    void spellCast(Hero hero, Spell spell, Monster target, double damage, String effectText);

    void attackDodged(String defenderName, String attackName);

    void monsterDefeated(Monster monster);

    void monsterAttacked(Monster monster, Hero target, double damage);

    void heroFainted(Hero hero);

    void roundEnded();

    void battleWon();

    void battleLost();

    void heroRewarded(Hero hero, int xp, int gold, boolean leveledUp);

    void heroRevived(Hero hero);
}
