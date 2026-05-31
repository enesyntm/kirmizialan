package net.yntm.kirmizialan;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.yntm.kirmizialan.event.*;
import net.yntm.kirmizialan.logic.AreaManager;
import net.yntm.kirmizialan.logic.AreaPersistence;
import net.yntm.kirmizialan.logic.TileManager;

public class Kirmizialan implements ModInitializer {

    @Override
    public void onInitialize() {

        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            AreaManager.reset();
            TileManager.reset();

            var data = AreaPersistence.load(server);
            if (data != null) {
                AreaPersistence.apply(data);
            }
        });

        ServerLifecycleEvents.SERVER_STOPPING.register(server ->
                AreaPersistence.save(server, AreaPersistence.snapshot()));

        PlayerJoinEvent.register();
        MobKillEvent.register();
        BlockUnlockOnStepEvent.register();
        SoftPushbackEvent.register();
        EnderPearlControlEvent.register();
        DimensionChangeEvent.register();
    }
}