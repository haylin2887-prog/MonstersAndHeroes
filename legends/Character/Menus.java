package legends.Character;

import java.util.ArrayList;

import legends.Console;
import legends.Market.Market;

// The inventory menus (equip, potions) and the market menu.
public class Menus {
    private Console console;

    public Menus(Console console) {
        this.console = console;
    }

    // ---------- inventory ----------
    // Returns true if a potion was used.
    public boolean usePotion(Hero hero) {
        ArrayList<Potion> potions = hero.getItems(Potion.class);
        if (potions.isEmpty()) {
            console.println("  " + hero.getName() + " has no potions.");
            return false;
        }
        Potion potion = console.chooseFrom("Choose a potion (0 to cancel):", potions);
        if (potion == null) {
            return false;
        }
        console.println("  " + potion.use(hero));
        hero.removeItem(potion);
        return true;
    }

    // Returns true if something was equipped.
    public boolean equip(Hero hero) {
        ArrayList<Item> gear = new ArrayList<Item>();
        gear.addAll(hero.getItems(Weapon.class));
        gear.addAll(hero.getItems(Armor.class));
        if (gear.isEmpty()) {
            console.println("  " + hero.getName() + " has no weapons or armor.");
            return false;
        }
        Item item = console.chooseFrom("Equip which item? (0 to cancel):", gear);
        if (item == null) {
            return false;
        }

        if (item instanceof Armor) {
            hero.equipArmor((Armor) item);
        } else {
            Weapon weapon = (Weapon) item;
            boolean bothHands = false;
            if (weapon.getHands() == 2) {
                bothHands = true;
            } else {
                int hands = console.readInt("  Hold with 1) one hand or 2) both hands (+50% damage)? ", 1, 2);
                if (hands == 2) {
                    bothHands = true;
                }
            }
            hero.equipWeapon(weapon, bothHands);
        }
        console.println("  " + hero.getName() + " equipped " + item.getName() + ".");
        return true;
    }

    // ---------- market ----------
    public void visitMarket(Market market, Party party) {
        console.println("=== Welcome to the market! ===");
        while (true) {
            console.println("Which hero is shopping?");
            ArrayList<Hero> heroes = party.getHeroes();
            for (int i = 0; i < heroes.size(); i++) {
                Hero hero = heroes.get(i);
                console.println("  " + (i + 1) + ") " + hero.getName() + " (Lv" + hero.getLevel()
                        + ", " + hero.getGold() + " gold)");
            }
            console.println("  0) Leave the market");
            int choice = console.readInt("> ", 0, heroes.size());
            if (choice == 0) {
                return;
            }
            shop(market, heroes.get(choice - 1));
        }
    }

    private void shop(Market market, Hero hero) {
        while (true) {
            console.println(hero.getName() + " has " + hero.getGold() + " gold.  1) Buy  2) Sell  0) Back");
            int choice = console.readInt("> ", 0, 2);
            switch (choice) {
                case 1:
                    buy(market, hero);
                    break;
                case 2:
                    sell(market, hero);
                    break;
                default:
                    return;
            }
        }
    }

    private void buy(Market market, Hero hero) {
        if (market.getStock().isEmpty()) {
            console.println("  Sold out!");
            return;
        }
        Item item = console.chooseFrom("Buy which? (0 to cancel):", market.getStock());
        if (item == null) {
            return;
        }
        String error = market.buy(hero, item);
        if (error == null) {
            console.println("  Bought " + item.getName() + " for " + item.getPrice() + " gold.");
        } else {
            console.println("  " + error);
        }
    }

    private void sell(Market market, Hero hero) {
        if (hero.getInventory().isEmpty()) {
            console.println("  Nothing to sell.");
            return;
        }
        Item item = console.chooseFrom("Sell which? (0 to cancel; you get half of the price shown):",
                hero.getInventory());
        if (item == null) {
            return;
        }
        market.sell(hero, item);
        console.println("  Sold " + item.getName() + " for " + item.sellPrice() + " gold.");
    }
}
