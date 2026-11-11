package legends.Character;

import legends.Rules;
// strategy pattern
interface SpellEffect {
    String apply(Monster target);

    String getName();
}

// A single-use magic attack. The extra effect (ice, fire, lightning) is a Strategy object.
public class Spell extends Item {
    private int damage;
    private int manaCost;
    private SpellEffect effect;

    public Spell(String name, int price, int level, int damage, int manaCost, SpellEffect effect) {
        super(name, price, level, 1);
        this.damage = damage;
        this.manaCost = manaCost;
        this.effect = effect;
    }

    public int getManaCost() {
        return manaCost;
    }

    // Damage grows with the caster's dexterity.
    public double damageFor(Hero caster) {
        double base = damage + (caster.getDexterity() / 10000.0) * damage;
        return base * Rules.SPELL_SCALE;
    }

    // The spell does not care which effect it has; it just asks the strategy.
    public String applyEffect(Monster target) {
        return effect.apply(target);
    }

    public Item copy() {
        return new Spell(name, price, level, damage, manaCost, effect);
    }

    public String describe() {
        return effect.getName() + " spell  damage " + damage + ", mana " + manaCost;
    }
}

class IceEffect implements SpellEffect {
    public String apply(Monster target) {
        target.reduceDamage(Rules.SPELL_EFFECT);
        return target.getName() + "'s damage was reduced!";
    }

    public String getName() {
        return "Ice";
    }
}

class LightningEffect implements SpellEffect {
    public String apply(Monster target) {
        target.reduceDodge(Rules.SPELL_EFFECT);
        return target.getName() + "'s dodge chance was reduced!";
    }

    public String getName() {
        return "Lightning";
    }
}

class FireEffect implements SpellEffect {
    public String apply(Monster target) {
        target.reduceDefense(Rules.SPELL_EFFECT);
        return target.getName() + "'s defense was reduced!";
    }

    public String getName() {
        return "Fire";
    }
}
