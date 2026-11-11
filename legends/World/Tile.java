package legends.World;

import legends.Market.Market;

public abstract class Tile {
    public abstract char symbol();

    public boolean isAccessible() {
        return true;
    }

    public boolean canTriggerBattle() {
        return false;
    }

    // Only market tiles return a market.
    public Market getMarket() {
        return null;
    }

    // ---------- the three kinds of tiles ----------
    public static class CommonTile extends Tile {
        public char symbol() {
            return ' ';
        }

        public boolean canTriggerBattle() {
            return true;
        }
    }

    public static class InaccessibleTile extends Tile {
        public char symbol() {
            return 'X';
        }

        public boolean isAccessible() {
            return false;
        }
    }

    public static class MarketTile extends Tile {
        private Market market;

        public MarketTile(Market market) {
            this.market = market;
        }

        public char symbol() {
            return 'M';
        }

        public Market getMarket() {
            return market;
        }
    }
}
