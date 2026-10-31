package com.jellas.noinventoryfacility;

import com.jellas.noinventoryfacility.hud.HudSystem;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;

@Mod(value = NoInventoryFacility.MODID, dist = Dist.CLIENT)
public class NoInventoryFacilityClient {

    public NoInventoryFacilityClient(IEventBus modEventBus) {
        modEventBus.addListener(OffHandDrop::register);

        NeoForge.EVENT_BUS.register(ItemDetectorSystem.class);
        NeoForge.EVENT_BUS.register(ClientInputSystem.class);
        NeoForge.EVENT_BUS.register(HudSystem.class);
        NeoForge.EVENT_BUS.addListener(OffHandDrop::tick);
    }
}
