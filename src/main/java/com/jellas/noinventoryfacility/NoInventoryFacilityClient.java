package com.jellas.noinventoryfacility;

import com.jellas.noinventoryfacility.hud.CarriedItemLayer;
import com.jellas.noinventoryfacility.hud.HudSystem;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.world.entity.player.PlayerModelType;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.renderstate.RegisterRenderStateModifiersEvent;
import com.jellas.noinventoryfacility.hud.TwoHandedHoldRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import com.jellas.noinventoryfacility.workbench.WorkbenchScreen;

@Mod(value = NoInventoryFacility.MODID, dist = Dist.CLIENT)
public class NoInventoryFacilityClient {

    public NoInventoryFacilityClient(IEventBus modEventBus) {
        modEventBus.addListener(OffHandDrop::register);

        // Third person: heavy items carried with both arms.
        modEventBus.addListener((RegisterRenderStateModifiersEvent event) ->
                event.registerAvatarEntityModifier(CarriedItemLayer.MODIFIER));
        modEventBus.addListener((EntityRenderersEvent.AddLayers event) -> {
            for (PlayerModelType skin : event.getSkins()) {
                AvatarRenderer<AbstractClientPlayer> renderer = event.getPlayerRenderer(skin);
                renderer.addLayer(new CarriedItemLayer(renderer));
            }
        });
        modEventBus.addListener((RegisterMenuScreensEvent event) ->
                event.register(NoInventoryFacility.WORKBENCH_MENU.get(), WorkbenchScreen::new));

        NeoForge.EVENT_BUS.register(ItemDetectorSystem.class);
        NeoForge.EVENT_BUS.register(ClientInputSystem.class);
        NeoForge.EVENT_BUS.register(HudSystem.class);
        NeoForge.EVENT_BUS.register(TwoHandedHoldRenderer.class);
        NeoForge.EVENT_BUS.addListener(OffHandDrop::tick);
    }
}
