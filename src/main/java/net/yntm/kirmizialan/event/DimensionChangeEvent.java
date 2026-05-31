package net.yntm.kirmizialan.event;

import net.fabricmc.fabric.api.entity.event.v1.ServerEntityWorldChangeEvents;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.yntm.kirmizialan.logic.AreaManager;

public class DimensionChangeEvent {

    public static void register() {
        ServerEntityWorldChangeEvents.AFTER_PLAYER_CHANGE_WORLD.register(
                (player, origin, destination) -> {
                    String dimKey = getDimKey(destination);
                    AreaManager.setCurrentDimension(dimKey);

                    if (!AreaManager.isInitialized(dimKey)) {
                        BlockPos pos = player.getBlockPos();

                        if (dimKey.equals("the_end")) {
                            pos = new BlockPos(100, 49, 0);
                        }

                        AreaManager.initFromReference(pos, dimKey);
                    }
                }
        );
    }

    public static String getDimKey(ServerWorld world) {
        return world.getRegistryKey().getValue().getPath();
        // overworld → "overworld"
        // nether   → "the_nether"
        // end      → "the_end"
    }
}