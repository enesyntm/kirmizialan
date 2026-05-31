package net.yntm.kirmizialan.event;

import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.yntm.kirmizialan.logic.AreaManager;

public class PlayerJoinEvent {

    public static void register() {
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            ServerPlayerEntity player = handler.getPlayer();

            ServerWorld world = server.getOverworld();
            String dim = DimensionChangeEvent.getDimKey(world);
            AreaManager.setCurrentDimension(dim);

            if (!AreaManager.isInitialized("overworld")) {
                AreaManager.init(player.getBlockPos(), "overworld");
            }
        });
    }
}