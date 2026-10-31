package com.jellas.noinventoryfacility;

import com.jellas.noinventoryfacility.network.NetworkHandler;
import com.jellas.noinventoryfacility.workbench.WorkbenchBlock;
import com.jellas.noinventoryfacility.workbench.WorkbenchBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;

@Mod(NoInventoryFacility.MODID)
public class NoInventoryFacility {

    public static final String MODID = "noinventoryfacility";

    public static final DeferredRegister.Blocks BLOCKS =
            DeferredRegister.createBlocks(MODID);

    public static final DeferredRegister.Items ITEMS =
            DeferredRegister.createItems(MODID);

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(
                    Registries.BLOCK_ENTITY_TYPE,
                    MODID
            );

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(
                    Registries.CREATIVE_MODE_TAB,
                    MODID
            );

    public static final DeferredBlock<WorkbenchBlock> WORKBENCH =
            BLOCKS.register(
                    "workbench",
                    registryName -> new WorkbenchBlock(
                            BlockBehaviour.Properties.of()
                                    .setId(ResourceKey.create(Registries.BLOCK, registryName))
                    )
            );

    public static final DeferredItem<BlockItem> WORKBENCH_ITEM =
            ITEMS.register(
                    "workbench",
                    registryName -> new BlockItem(
                            WORKBENCH.get(),
                            new Item.Properties()
                                    .setId(ResourceKey.create(Registries.ITEM, registryName))
                    )
            );

    public static final DeferredHolder<
            BlockEntityType<?>,
            BlockEntityType<WorkbenchBlockEntity>
            > WORKBENCH_ENTITY =
            BLOCK_ENTITIES.register(
                    "workbench",
                    () -> new BlockEntityType<>(
                            WorkbenchBlockEntity::new,
                            WORKBENCH.get()
                    )
            );

    public NoInventoryFacility(IEventBus modEventBus) {
        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);
        BLOCK_ENTITIES.register(modEventBus);
        CREATIVE_MODE_TABS.register(modEventBus);

        modEventBus.register(NetworkHandler.class);

        // Common (client + server) only. Client-only systems are registered
        // in NoInventoryFacilityClient, otherwise a dedicated server crashes.
        NeoForge.EVENT_BUS.register(InventorySystem.class);
    }
}