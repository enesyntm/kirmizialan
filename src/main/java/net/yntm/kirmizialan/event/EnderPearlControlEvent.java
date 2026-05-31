package net.yntm.kirmizialan.event;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.minecraft.entity.projectile.thrown.EnderPearlEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.yntm.kirmizialan.logic.AreaManager;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class EnderPearlControlEvent {

    // 🔒 Pearl atmadan önceki güvenli konum
    private static final Map<UUID, Vec3d> LAST_SAFE_POS = new HashMap<>();

    public static void register() {

        // 📌 Pearl spawn olduğunda → oyuncunun konumunu kaydet
        ServerEntityEvents.ENTITY_LOAD.register((entity, world) -> {
            if (!(entity instanceof EnderPearlEntity pearl)) return;
            if (!(pearl.getOwner() instanceof ServerPlayerEntity player)) return;

            LAST_SAFE_POS.put(
                    player.getUuid(),
                    new Vec3d(player.getX(), player.getY(), player.getZ())
            );
        });

        // 📌 Pearl yok olurken (çarptı)
        ServerEntityEvents.ENTITY_UNLOAD.register((entity, world) -> {
            if (!(entity instanceof EnderPearlEntity pearl)) return;
            if (!(pearl.getOwner() instanceof ServerPlayerEntity player)) return;

            BlockPos hitPos = new BlockPos(
                    (int) Math.floor(pearl.getX()),
                    (int) Math.floor(pearl.getY()),
                    (int) Math.floor(pearl.getZ())
            );

            // ✅ Alan içi → serbest
            if (AreaManager.isUnlocked(hitPos)) return;

            // ❌ Alan dışı → geri ışınla
            Vec3d safe = LAST_SAFE_POS.get(player.getUuid());
            if (safe == null) return;

            player.requestTeleport(
                    safe.x,
                    safe.y,
                    safe.z
            );
        });
    }
}
