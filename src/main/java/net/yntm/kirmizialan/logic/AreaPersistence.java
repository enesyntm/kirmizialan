package net.yntm.kirmizialan.logic;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.WorldSavePath;
import net.minecraft.util.math.BlockPos;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.HashSet;
import java.util.Set;

public final class AreaPersistence {
    private AreaPersistence() {}

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final String FILE_NAME = "kirmizialan_state.json";

    public static class DimData {
        public long origin = Long.MIN_VALUE;
        public Set<Long> unlocked = new HashSet<>();
    }

    public static class SaveData {
        public DimData overworld  = new DimData();
        public DimData nether     = new DimData();
        public DimData end        = new DimData();
        public int tiles = 0;
    }

    private static Path getFile(MinecraftServer server) {
        return server.getSavePath(WorldSavePath.ROOT).resolve(FILE_NAME);
    }

    public static SaveData load(MinecraftServer server) {
        Path file = getFile(server);
        if (!Files.exists(file)) return null;
        try (BufferedReader br = Files.newBufferedReader(file)) {
            return GSON.fromJson(br, SaveData.class);
        } catch (Exception e) {
            return null;
        }
    }

    public static void save(MinecraftServer server, SaveData data) {
        if (server == null || data == null) return;
        Path file = getFile(server);
        Path temp = file.resolveSibling(FILE_NAME + ".tmp");
        try (BufferedWriter bw = Files.newBufferedWriter(temp)) {
            GSON.toJson(data, bw);
            Files.move(temp, file, StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception e) {
            // sessiz hata
        }
    }

    public static SaveData snapshot() {
        SaveData d = new SaveData();
        d.overworld = dimSnapshot("overworld");
        d.nether    = dimSnapshot("the_nether");
        d.end       = dimSnapshot("the_end");
        d.tiles     = TileManager.getTiles();
        return d;
    }

    private static DimData dimSnapshot(String dim) {
        DimData dd = new DimData();
        BlockPos origin = AreaManager.getOrigin(dim);
        if (origin != null) dd.origin = origin.asLong();
        for (BlockPos p : AreaManager.getUnlocked(dim)) {
            dd.unlocked.add(p.asLong());
        }
        return dd;
    }

    public static void apply(SaveData d) {
        if (d == null) return;
        applyDim(d.overworld, "overworld");
        applyDim(d.nether,    "the_nether");
        applyDim(d.end,       "the_end");
        TileManager.setTiles(d.tiles);
    }

    private static void applyDim(DimData dd, String dim) {
        if (dd == null) return;
        if (dd.origin == Long.MIN_VALUE) return;

        AreaManager.setOrigin(BlockPos.fromLong(dd.origin), dim);
        AreaManager.clearUnlocked(dim);
        for (Long l : dd.unlocked) {
            if (l != null) AreaManager.addUnlocked(BlockPos.fromLong(l), dim);
        }
        AreaManager.markInitialized(dim);
    }
}