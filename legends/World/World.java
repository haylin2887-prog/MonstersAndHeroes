package legends.World;

import java.util.ArrayList;
import java.util.Collections;

import legends.Rules;
import legends.Character.GameData;
import legends.Character.Party;

public class World {
    private int size;
    private Tile[][] grid;

    // Makes random maps until every walkable tile can be reached.
    public World(int size, GameData data) {
        this.size = size;
        this.grid = new Tile[size][size];
        boolean connected = false;
        while (!connected) {
            generate(data);
            connected = isConnected();
        }
    }

    private void generate(GameData data) {
        int total = size * size;
        int blocked = (int) Math.round(total * Rules.PCT_INACCESSIBLE);
        int markets = (int) Math.round(total * Rules.PCT_MARKET);

        // 0 = common, 1 = market, 2 = inaccessible
        ArrayList<Integer> kinds = new ArrayList<Integer>();
        for (int i = 0; i < total; i++) {
            if (i < blocked) {
                kinds.add(2);
            } else if (i < blocked + markets) {
                kinds.add(1);
            } else {
                kinds.add(0);
            }
        }
        Collections.shuffle(kinds, Rules.getRandom());

        for (int i = 0; i < total; i++) {
            int kind = kinds.get(i);
            Tile tile;
            if (kind == 2) {
                tile = new Tile.InaccessibleTile();
            } else if (kind == 1) {
                tile = new Tile.MarketTile(data.createMarket());
            } else {
                tile = new Tile.CommonTile();
            }
            grid[i / size][i % size] = tile;
        }
    }

    private boolean isConnected() {
        int[] start = findCommonTile();
        if (start == null) {
            return false;
        }
        boolean[][] visited = new boolean[size][size];
        fill(start[0], start[1], visited);

        // every accessible tile must have been visited
        for (int r = 0; r < size; r++) {
            for (int c = 0; c < size; c++) {
                if (grid[r][c].isAccessible() && !visited[r][c]) {
                    return false;
                }
            }
        }
        return true;
    }

    // Flood fill: visit this tile and all walkable neighbours.
    private void fill(int r, int c, boolean[][] visited) {
        if (!inBounds(r, c)) {
            return;
        }
        if (visited[r][c]) {
            return;
        }
        if (!grid[r][c].isAccessible()) {
            return;
        }
        visited[r][c] = true;
        fill(r + 1, c, visited);
        fill(r - 1, c, visited);
        fill(r, c + 1, visited);
        fill(r, c - 1, visited);
    }

    // First common tile, used as the starting point of the party.
    public int[] findCommonTile() {
        for (int r = 0; r < size; r++) {
            for (int c = 0; c < size; c++) {
                if (grid[r][c] instanceof Tile.CommonTile) {
                    return new int[] { r, c };
                }
            }
        }
        return null;
    }

    public boolean inBounds(int r, int c) {
        return r >= 0 && c >= 0 && r < size && c < size;
    }

    public Tile getTile(int r, int c) {
        return grid[r][c];
    }

    public String render(Party party) {
        String line = "+";
        for (int c = 0; c < size; c++) {
            line = line + "---+";
        }
        String text = line + "\n";
        for (int r = 0; r < size; r++) {
            text = text + "|";
            for (int c = 0; c < size; c++) {
                char symbol = grid[r][c].symbol();
                String cell = " " + symbol + " ";
                if (party.getRow() == r && party.getCol() == c) {
                    if (symbol == 'M') {
                        cell = "P/M";
                    } else {
                        cell = " P ";
                    }
                }
                text = text + cell + "|";
            }
            text = text + "\n" + line + "\n";
        }
        return text + "P = your party   M = market   X = blocked   blank = wilderness (monsters!)";
    }
}
