package net.yntm.kirmizialan.logic;

import net.minecraft.util.math.BlockPos;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class AreaManager {

    private static final Map<String, BlockPos> origins = new HashMap<>();
    private static final Map<String, Set<BlockPos>> unlockedMap = new HashMap<>();
    private static final Set<String> initializedDimensions = new HashSet<>();

    private static String currentDimension = "overworld";

    public static void setCurrentDimension(String dim) {
        currentDimension = dim;
    }

    @SuppressWarnings("unused")
    public static String getCurrentDimension() {
        return currentDimension;
    }

    // ─── INIT ───────────────────────────────────────────

    // Overworld için — 3x3 ile başla
    @SuppressWarnings("unused")
    public static void init(BlockPos start) {
        init(start, currentDimension);
    }

    // Overworld ilk init — 3x3
    public static void init(BlockPos start, String dim) {
        BlockPos origin = start.toImmutable();
        origins.put(dim, origin);

        Set<BlockPos> unlocked = new HashSet<>();
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                unlocked.add(origin.add(x, 0, z));
            }
        }
        unlockedMap.put(dim, unlocked);
        initializedDimensions.add(dim);
    }

    // Nether/End için — overworld'deki şekli kopyala, origin değiştir
    public static void initFromReference(BlockPos newOrigin, String dim) {
        BlockPos origin = newOrigin.toImmutable();
        origins.put(dim, origin);

        // Overworld'deki origin ve unlocked seti al
        BlockPos overworldOrigin = origins.get("overworld");
        Set<BlockPos> overworldUnlocked = unlockedMap.getOrDefault("overworld", new HashSet<>());

        Set<BlockPos> unlocked = new HashSet<>();

        if (overworldOrigin != null) {
            for (BlockPos p : overworldUnlocked) {
                // Overworld'deki relative offset'i hesapla
                int dx = p.getX() - overworldOrigin.getX();
                int dz = p.getZ() - overworldOrigin.getZ();
                // Yeni origin'e uygula
                unlocked.add(new BlockPos(origin.getX() + dx, origin.getY(), origin.getZ() + dz));
            }
        } else {
            // Overworld yoksa 3x3 başlat
            for (int x = -1; x <= 1; x++) {
                for (int z = -1; z <= 1; z++) {
                    unlocked.add(origin.add(x, 0, z));
                }
            }
        }

        unlockedMap.put(dim, unlocked);
        initializedDimensions.add(dim);
    }

    @SuppressWarnings({"unused", "BooleanMethodIsAlwaysInverted"})
    public static boolean isInitialized() {
        return initializedDimensions.contains(currentDimension);
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static boolean isInitialized(String dim) {
        return initializedDimensions.contains(dim);
    }

    @SuppressWarnings("unused")
    public static void markInitialized() {
        initializedDimensions.add(currentDimension);
    }

    public static void markInitialized(String dim) {
        initializedDimensions.add(dim);
    }

    // ─── UNLOCK ─────────────────────────────────────────

    public static void unlock(BlockPos pos) {
        BlockPos flatPos = flat(pos);
        getUnlockedSet(currentDimension).add(flatPos);

        // Mevcut boyuttaki origin'e göre offset hesapla
        BlockPos currentOrigin = origins.get(currentDimension);
        if (currentOrigin == null) return;

        int dx = flatPos.getX() - currentOrigin.getX();
        int dz = flatPos.getZ() - currentOrigin.getZ();

        // Diğer tüm initialized boyutlara da ekle
        for (String dim : initializedDimensions) {
            if (dim.equals(currentDimension)) continue;

            BlockPos otherOrigin = origins.get(dim);
            if (otherOrigin == null) continue;

            BlockPos otherPos = new BlockPos(
                    otherOrigin.getX() + dx,
                    otherOrigin.getY(),
                    otherOrigin.getZ() + dz
            );
            getUnlockedSet(dim).add(otherPos);
        }
    }

    public static boolean isUnlocked(BlockPos pos) {
        return getUnlockedSet(currentDimension).contains(flat(pos));
    }

    public static boolean isAdjacentToUnlocked(BlockPos pos) {
        BlockPos p = flat(pos);
        Set<BlockPos> set = getUnlockedSet(currentDimension);
        return set.contains(p.north()) ||
                set.contains(p.south()) ||
                set.contains(p.east())  ||
                set.contains(p.west());
    }

    public static Set<BlockPos> getUnlocked() {
        return Collections.unmodifiableSet(getUnlockedSet(currentDimension));
    }

    // ─── PERSISTENCE HELPERS ────────────────────────────

    public static void setOrigin(BlockPos pos, String dim) {
        origins.put(dim, pos == null ? null : pos.toImmutable());
    }

    public static void clearUnlocked(String dim) {
        unlockedMap.put(dim, new HashSet<>());
    }

    public static void addUnlocked(BlockPos pos, String dim) {
        if (pos != null) getUnlockedSet(dim).add(pos.toImmutable());
    }

    public static BlockPos getOrigin() {
        return origins.get(currentDimension);
    }

    public static BlockPos getOrigin(String dim) {
        return origins.get(dim);
    }

    public static Set<BlockPos> getUnlocked(String dim) {
        return Collections.unmodifiableSet(getUnlockedSet(dim));
    }

    // ─── RESET ──────────────────────────────────────────

    public static void reset() {
        origins.clear();
        unlockedMap.clear();
        initializedDimensions.clear();
        currentDimension = "overworld";
    }

    // ─── PRIVATE ────────────────────────────────────────

    private static Set<BlockPos> getUnlockedSet(String dim) {
        return unlockedMap.computeIfAbsent(dim, k -> new HashSet<>());
    }

    private static BlockPos flat(BlockPos pos) {
        BlockPos origin = origins.get(currentDimension);
        int y = origin != null ? origin.getY() : pos.getY();
        return new BlockPos(pos.getX(), y, pos.getZ());
    }
}