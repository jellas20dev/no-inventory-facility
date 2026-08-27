package com.jellas.noinventoryfacility;

import com.jellas.noinventoryfacility.hud.HudSystem;
import com.jellas.noinventoryfacility.network.NetworkHandler;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.registries.DeferredRegister;

@Mod(NoInventoryFacility.MODID)
public class NoInventoryFacility {

    public static final String MODID = "noinventoryfacility";
    public static final DeferredRegister.Blocks BLOCKS =
            DeferredRegister.createBlocks(MODID);
    public static final DeferredRegister.Items ITEMS =
            DeferredRegister.createItems(MODID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(
                    Registries.CREATIVE_MODE_TAB,
                    MODID
            );

    public NoInventoryFacility(IEventBus modEventBus) {

        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);
        CREATIVE_MODE_TABS.register(modEventBus);

        modEventBus.register(NetworkHandler.class);

        InventorySystem.init();

        NeoForge.EVENT_BUS.register(InventorySystem.class);
        NeoForge.EVENT_BUS.register(HudSystem.class);

        modEventBus.addListener(
                OffHandDrop::register
        );

        NeoForge.EVENT_BUS.addListener(
                OffHandDrop::tick
        );
    }
}