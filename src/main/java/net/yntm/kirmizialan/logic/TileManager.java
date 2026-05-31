package net.yntm.kirmizialan.logic;

import net.minecraft.server.network.ServerPlayerEntity;
import net.yntm.kirmizialan.data.TileData;

public class TileManager {

    private static final TileData tileData = new TileData();

    @SuppressWarnings("unused")
    public static void onMobKilled(ServerPlayerEntity player) {
        tileData.addTile(1);
    }

    public static int getTiles() {
        return tileData.getTiles();
    }

    public static void consumeTile() {
        tileData.consumeTile();
    }

    public static void setTiles(int value) {
        tileData.setTiles(value);
    }

    public static void reset() {
        tileData.setTiles(0);
    }
}