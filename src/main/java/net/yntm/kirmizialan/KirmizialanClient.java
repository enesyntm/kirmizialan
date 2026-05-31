package net.yntm.kirmizialan;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.yntm.kirmizialan.client.TileHudRenderer;
import net.yntm.kirmizialan.logic.AreaBorderRenderer;

@SuppressWarnings("deprecation")
public class KirmizialanClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> AreaBorderRenderer.render());
        HudRenderCallback.EVENT.register(new TileHudRenderer());
    }
}