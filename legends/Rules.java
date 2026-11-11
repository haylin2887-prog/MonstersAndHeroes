package legends;

import java.util.Random;

// All the numbers of the game are kept here, so they are easy to change.
public class Rules {
    public static final int MIN_PARTY = 1;
    public static final int MAX_PARTY = 3;
    public static final int WORLD_SIZE = 8;
    public static final double PCT_INACCESSIBLE = 0.20;
    public static final double PCT_MARKET = 0.30;
    public static final double BATTLE_CHANCE = 0.7;

    public static final int HP_PER_LEVEL = 100;
    public static final int XP_PER_LEVEL = 10;
    public static final double LEVEL_UP_SKILL_GAIN = 0.05;
    public static final double FAVORED_EXTRA_GAIN = 0.05;
    public static final double FAVORED_START_BONUS = 0.10;
    public static final double MP_LEVEL_UP = 1.1;
    public static final double REGEN = 1.1;

    public static final double ATTACK_FACTOR = 0.05;
    public static final double TWO_HAND_BONUS = 1.5;
    public static final double HERO_DODGE_FACTOR = 0.002;
    public static final double MONSTER_DODGE_FACTOR = 0.01;
    public static final double MAX_DODGE = 0.5;
    public static final double DEFENSE_FACTOR = 0.05;
    public static final double ARMOR_FACTOR = 0.05;
    public static final double SPELL_SCALE = 0.1;
    public static final double SPELL_EFFECT = 0.1;
    public static final double MONSTER_DAMAGE_FACTOR = 0.2;
    public static final double MONSTER_FAVORED_BONUS = 1.1;

    public static final int XP_PER_MONSTER_LEVEL = 2;
    public static final int GOLD_PER_MONSTER_LEVEL = 100;
    public static final int WEAPON_USES = 10;
    public static final int ARMOR_USES = 10;

    private static Random random = new Random();

    private Rules() {
    }

   
    public static boolean chance(double probability) {
        return random.nextDouble() < probability;
    }

    public static int randomInt(int bound) {
        return random.nextInt(bound);
    }

    public static Random getRandom() {
        return random;
    }
}
