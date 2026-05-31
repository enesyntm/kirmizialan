package net.yntm.kirmizialan.event;

import net.fabricmc.fabric.api.entity.event.v1.ServerEntityCombatEvents;
import net.minecraft.server.network.ServerPlayerEntity;
import net.yntm.kirmizialan.logic.TileManager;

public class MobKillEvent {

    public static void register() {
        ServerEntityCombatEvents.AFTER_KILLED_OTHER_ENTITY.register(
                (world, entity, killedEntity, damageSource) -> {
                    if (!(entity instanceof ServerPlayerEntity player)) return;
                    if (killedEntity instanceof ServerPlayerEntity) return; // PvP'de tile kazanma
                    TileManager.onMobKilled(player);
                }
        );
    }
}