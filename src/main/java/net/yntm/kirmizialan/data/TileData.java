package net.yntm.kirmizialan.data;

public class TileData {

    private int tiles = 0;

    public int getTiles() {
        return tiles;
    }

    public void addTile(int amount) {
        tiles += amount;
    }

    public void consumeTile() {
        if (tiles > 0) tiles--;
    }

    public void setTiles(int value) {
        this.tiles = Math.max(0, value);
    }
}