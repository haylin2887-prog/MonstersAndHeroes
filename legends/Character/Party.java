package legends.Character;

import java.util.ArrayList;

public class Party {
    private ArrayList<Hero> heroes = new ArrayList<Hero>();
    private int row;
    private int col;

    public void addHero(Hero hero) {
        heroes.add(hero);
    }

    public ArrayList<Hero> getHeroes() {
        return heroes;
    }

    public int size() {
        return heroes.size();
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public void moveTo(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public boolean allFainted() {
        for (Hero hero : heroes) {
            if (!hero.isFainted()) {
                return false;
            }
        }
        return true;
    }

    public int highestLevel() {
        int highest = 1;
        for (Hero hero : heroes) {
            if (hero.getLevel() > highest) {
                highest = hero.getLevel();
            }
        }
        return highest;
    }
}
