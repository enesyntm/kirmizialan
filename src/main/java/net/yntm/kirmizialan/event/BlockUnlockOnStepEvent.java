package net.yntm.kirmizialan.event;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.yntm.kirmizialan.logic.AreaManager;
import net.yntm.kirmizialan.logic.AreaPersistence;
import net.yntm.kirmizialan.logic.TileManager;

public class BlockUnlockOnStepEvent {

    public static void register() {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                handlePlayerStep(server, player);
            }
        });
    }

    private static void handlePlayerStep(
            MinecraftServer server,
            ServerPlayerEntity player
    ) {
        BlockPos blockUnder = player.getBlockPos().down();

        if (AreaManager.isUnlocked(blockUnder)) return;
        if (!AreaManager.isAdjacentToUnlocked(blockUnder)) return;
        if (TileManager.getTiles() <= 0) return;

        AreaManager.unlock(blockUnder);
        TileManager.consumeTile();

        // ✅ DOĞRU SAVE
        AreaPersistence.save(server, AreaPersistence.snapshot());

//                "[KIRMIZI ALAN] Blok açıldı: " + blockUnder +
//                        " | Kalan tile: " + TileManager.getTiles());
    }
}
