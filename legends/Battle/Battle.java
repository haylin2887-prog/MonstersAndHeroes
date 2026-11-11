package legends.Battle;

import java.util.ArrayList;

import legends.Console;
import legends.Rules;
import legends.Character.Hero;
import legends.Character.Menus;
import legends.Character.Monster;
import legends.Character.Party;
import legends.Character.Spell;

// A team fight: every living hero chooses an action, then every monster attacks.
// Battle does not print results itself; it tells its observers.
public class Battle {
    private Party party;
    private ArrayList<Monster> monsters;
    private ArrayList<Monster> defeated = new ArrayList<Monster>();
    private Console console;
    private Menus menus;
    private ArrayList<BattleObserver> observers = new ArrayList<BattleObserver>();

    public Battle(Party party, ArrayList<Monster> monsters, Console console, Menus menus) {
        this.party = party;
        this.monsters = monsters;
        this.console = console;
        this.menus = menus;
    }

    public void addObserver(BattleObserver observer) {
        observers.add(observer);
    }

    // Returns true if the heroes win.
    public boolean run() {
        for (BattleObserver o : observers) {
            o.battleStarted();
        }
        showStats();

        int round = 1;
        while (true) {
            for (BattleObserver o : observers) {
                o.roundStarted(round);
            }
            round = round + 1;

            // heroes move first
            for (Hero hero : party.getHeroes()) {
                if (hero.isFainted() || monsters.isEmpty()) {
                    continue;
                }
                heroTurn(hero);
            }
            if (monsters.isEmpty()) {
                victory();
                return true;
            }

            // then the monsters
            monstersTurn();
            if (party.allFainted()) {
                for (BattleObserver o : observers) {
                    o.battleLost();
                }
                return false;
            }

            // end of the round
            for (Hero hero : party.getHeroes()) {
                hero.regenerate();
            }
            for (BattleObserver o : observers) {
                o.roundEnded();
            }
        }
    }

    // ---------- heroes ----------
    private void heroTurn(Hero hero) {
        boolean turnUsed = false;
        while (!turnUsed) {
            console.println();
            console.println(hero.statusLine(true));
            console.println("  1) Attack  2) Cast spell  3) Use potion  4) Equip  5) Show stats");
            int choice = console.readInt("> ", 1, 5);
            switch (choice) {
                case 1:
                    turnUsed = attack(hero);
                    break;
                case 2:
                    turnUsed = castSpell(hero);
                    break;
                case 3:
                    turnUsed = menus.usePotion(hero);
                    break;
                case 4:
                    turnUsed = menus.equip(hero);
                    break;
                default:
                    showStats();   // does not use the turn
                    break;
            }
        }
    }

    private boolean attack(Hero hero) {
        Monster target = chooseTarget();
        if (target == null) {
            return false;
        }
        if (target.tryDodge()) {
            for (BattleObserver o : observers) {
                o.attackDodged(target.getName(), hero.getName() + "'s attack");
            }
            return true;
        }
        double damage = target.afterDefense(hero.attackDamage());
        target.takeDamage(damage);
        for (BattleObserver o : observers) {
            o.heroAttacked(hero, target, damage);
        }
        hero.weaponUsed();
        checkDefeated(target);
        return true;
    }

    private boolean castSpell(Hero hero) {
        ArrayList<Spell> spells = hero.getItems(Spell.class);
        if (spells.isEmpty()) {
            console.println("  " + hero.getName() + " has no spells.");
            return false;
        }
        Spell spell = console.chooseFrom("Cast which? (0 to cancel):", spells);
        if (spell == null) {
            return false;
        }
        if (hero.getMp() < spell.getManaCost()) {
            console.println("  Not enough mana!");
            return false;
        }
        Monster target = chooseTarget();
        if (target == null) {
            return false;
        }

        
        
        hero.spendMana(spell.getManaCost());
        hero.removeItem(spell);   // spells can only be used once
        if (target.tryDodge()) {
            for (BattleObserver o : observers) {
                o.attackDodged(target.getName(), spell.getName());
            }
            return true;
        }
        double damage = target.afterDefense(spell.damageFor(hero));
        target.takeDamage(damage);
        String effectText = spell.applyEffect(target);
        for (BattleObserver o : observers) {
            o.spellCast(hero, spell, target, damage, effectText);
        }
        checkDefeated(target);
        return true;
    }

    private void checkDefeated(Monster monster) {
        if (monster.isFainted()) {
            monsters.remove(monster);
            defeated.add(monster);
            for (BattleObserver o : observers) {
                o.monsterDefeated(monster);
            }
        }
    }

    // Returns null if the player cancels.
    private Monster chooseTarget() {
        if (monsters.size() == 1) {
            return monsters.get(0);
        }
        return console.chooseFrom("Choose a target (0 to cancel):", monsters);
    }

    // ---------- monsters ----------
    private void monstersTurn() {
        for (Monster monster : monsters) {
            ArrayList<Hero> alive = new ArrayList<Hero>();
            for (Hero hero : party.getHeroes()) {
                if (!hero.isFainted()) {
                    alive.add(hero);
                }
            }
            if (alive.isEmpty()) {
                return;
            }
            
            
            Hero target = alive.get(Rules.randomInt(alive.size()));
            if (target.tryDodge()) {
                for (BattleObserver o : observers) {
                    o.attackDodged(target.getName(), monster.getName() + "'s attack");
                }
                continue;
            }
            double damage = target.receiveDamage(monster.attackDamage());
            for (BattleObserver o : observers) {
                o.monsterAttacked(monster, target, damage);
            }
            if (target.isFainted()) {
                for (BattleObserver o : observers) {
                    o.heroFainted(target);
                }
            }
        }
    }

    // ---------- the end of the battle ----------
    private void victory() {
        int xp = 0;
        int gold = 0;
        for (Monster monster : defeated) {
            
            xp = xp + monster.getLevel() * Rules.XP_PER_MONSTER_LEVEL;
            gold = gold + monster.getLevel() * Rules.GOLD_PER_MONSTER_LEVEL;
        }
        
        
        for (BattleObserver o : observers) {
           
            o.battleWon();
        }
        for (Hero hero : party.getHeroes()) {
            if (hero.isFainted()) {
                hero.revive();
                for (BattleObserver o : observers) {
                    o.heroRevived(hero);
                }
            } else {
                hero.earn(gold);
                boolean leveledUp = hero.gainExperience(xp);
                for (BattleObserver o : observers) {
                    o.heroRewarded(hero, xp, gold, leveledUp);
                }
            }
        }
    }

    private void showStats() {
        console.println("--- Heroes ---");
        for (Hero hero : party.getHeroes()) {
            console.println("  " + hero.statusLine(true));
        }
        console.println("--- Monsters ---");
        for (Monster monster : monsters) {
            console.println("  " + monster.statusLine());
        }
    }
}
