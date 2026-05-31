package net.yntm.kirmizialan.logic;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.HashSet;
import java.util.Set;

public class AreaBorderRenderer {

    private static final DustParticleEffect RED_DUST =
            new DustParticleEffect(0xFF0000, 0.6f);

    public static void render() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.world == null) return;
        if (client.player == null) return;
        if (!AreaManager.isInitialized()) return;
        if (AreaManager.getOrigin() == null) return;

        if (client.world.getTime() % 6 != 0) return;

        ParticleManager particles = client.particleManager;

        int y = client.player.getBlockPos().getY();
        double yOffset = y + 0.05;

        BlockPos playerPos = client.player.getBlockPos();
        int renderDistance = 32;

        // Snapshot al — ConcurrentModificationException önler
        Set<BlockPos> snapshot = new HashSet<>(AreaManager.getUnlocked());

        for (BlockPos block : snapshot) {
            if (Math.abs(block.getX() - playerPos.getX()) > renderDistance) continue;
            if (Math.abs(block.getZ() - playerPos.getZ()) > renderDistance) continue;

            BlockPos base = new BlockPos(block.getX(), y, block.getZ());

            for (Direction dir : Direction.Type.HORIZONTAL) {
                BlockPos neighbor = base.offset(dir);
                if (!AreaManager.isUnlocked(neighbor)) {
                    drawEdge(particles, base, dir, yOffset);
                }
            }
        }
    }

    private static void drawEdge(
            ParticleManager particles,
            BlockPos block,
            Direction dir,
            double y
    ) {
        double step = 1.0 / 3.0;

        if (dir == Direction.NORTH) {
            for (double x = 0; x <= 1; x += step)
                spawn(particles, block.getX() + x, y, block.getZ());
        }
        if (dir == Direction.SOUTH) {
            for (double x = 0; x <= 1; x += step)
                spawn(particles, block.getX() + x, y, block.getZ() + 1);
        }
        if (dir == Direction.WEST) {
            for (double z = 0; z <= 1; z += step)
                spawn(particles, block.getX(), y, block.getZ() + z);
        }
        if (dir == Direction.EAST) {
            for (double z = 0; z <= 1; z += step)
                spawn(particles, block.getX() + 1, y, block.getZ() + z);
        }
    }

    private static void spawn(
            ParticleManager particles,
            double x,
            double y,
            double z
    ) {
        particles.addParticle(RED_DUST, x, y, z, 0, 0, 0);
    }
}