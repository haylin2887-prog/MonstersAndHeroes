package legends.Market;

import java.util.ArrayList;

import legends.Character.Hero;
import legends.Character.Item;

public class Market {
    private ArrayList<Item> stock;

    public Market(ArrayList<Item> stock) {
        this.stock = stock;
    }

    public ArrayList<Item> getStock() {
        return stock;
    }

    // Returns null if the purchase worked, otherwise the reason it failed.
    public String buy(Hero hero, Item item) {
        if (item.getLevel() > hero.getLevel()) {
            return "You need to be level " + item.getLevel() + " to buy that.";
        }
        if (!hero.canAfford(item.getPrice())) {
            return "Not enough gold.";
        }
        hero.spend(item.getPrice());
        stock.remove(item);
        hero.addItem(item);
        return null;
    }

    public void sell(Hero hero, Item item) {
        hero.removeItem(item);
        hero.earn(item.sellPrice());
        stock.add(item);
    }
}
