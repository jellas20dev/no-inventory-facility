package com.jellas.noinventoryfacility;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;

@Mod(value = NoInventoryFacility.MODID, dist = Dist.CLIENT)
public class NoInventoryFacilityClient {

    public NoInventoryFacilityClient(IEventBus modEventBus) {
        NeoForge.EVENT_BUS.register(ItemDetectorSystem.class);
    }
}
