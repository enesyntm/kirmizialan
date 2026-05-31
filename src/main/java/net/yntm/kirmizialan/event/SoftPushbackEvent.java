package net.yntm.kirmizialan.event;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.yntm.kirmizialan.logic.AreaManager;

public class SoftPushbackEvent {

    private static final double EDGE_TOLERANCE = 0.30;

    public static void register() {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                for (ServerWorld world : server.getWorlds()) {
                    if (world.getPlayers().contains(player)) {
                        String dim = DimensionChangeEvent.getDimKey(world);
                        AreaManager.setCurrentDimension(dim);
                        break;
                    }
                }
                handle(player);
            }
        });
    }

    private static void handle(ServerPlayerEntity player) {
        if (!AreaManager.isInitialized()) return;
        if (AreaManager.getOrigin() == null) return;

        double px = player.getX();
        double pz = player.getZ();

        BlockPos playerXZ = new BlockPos(
                (int) Math.floor(px),
                AreaManager.getOrigin().getY(),
                (int) Math.floor(pz)
        );

        if (AreaManager.isUnlocked(playerXZ)) return;

        BlockPos closest = null;
        double bestDist = Double.MAX_VALUE;

        for (BlockPos u : AreaManager.getUnlocked()) {
            double cx = u.getX() + 0.5;
            double cz = u.getZ() + 0.5;
            double dx = cx - px;
            double dz = cz - pz;
            double dist = dx * dx + dz * dz;

            if (dist < bestDist) {
                bestDist = dist;
                closest = u;
            }
        }

        if (closest == null) return;

        double minX = closest.getX() + EDGE_TOLERANCE;
        double maxX = closest.getX() + 1 - EDGE_TOLERANCE;
        double minZ = closest.getZ() + EDGE_TOLERANCE;
        double maxZ = closest.getZ() + 1 - EDGE_TOLERANCE;

        double clampedX = Math.max(minX, Math.min(px, maxX));
        double clampedZ = Math.max(minZ, Math.min(pz, maxZ));

        if (Math.abs(clampedX - px) > 0.01 || Math.abs(clampedZ - pz) > 0.01) {
            player.requestTeleport(clampedX, player.getY(), clampedZ);
        }
    }
}