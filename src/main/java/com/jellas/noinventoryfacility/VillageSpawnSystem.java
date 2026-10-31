package com.jellas.noinventoryfacility;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.PlayerSpawnFinder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.StructureTags;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.LevelData;
import net.minecraft.world.level.storage.ServerLevelData;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
import org.slf4j.Logger;
import com.mojang.logging.LogUtils;

/**
 * Without an inventory there is no 2x2 crafting grid, so a new world starts
 * in a village: every village has a workbench in its town centre (see
 * VillageWorkbenchProcessor), where everything else is crafted.
 */
public class VillageSpawnSystem {

    private static final Logger LOGGER = LogUtils.getLogger();

    /**
     * Search radius in village placement cells (34 chunks each), about
     * 1600 blocks. Worlds without a village that close spawn as vanilla.
     */
    private static final int SEARCH_RADIUS = 3;

    /** Same search area vanilla uses around its own spawn chunk. */
    private static final int SPIRAL_SIZE = 11;

    @SubscribeEvent
    public static void onCreateSpawn(LevelEvent.CreateSpawnPosition event) {

        if (!(event.getLevel() instanceof ServerLevel level)
                || level.dimension() != Level.OVERWORLD) {
            return;
        }

        BlockPos origin = level.getChunkSource()
                .randomState()
                .sampler()
                .findSpawnPosition();

        BlockPos village = level.findNearestMapStructure(
                StructureTags.VILLAGE,
                origin,
                SEARCH_RADIUS,
                false
        );

        if (village == null) {
            LOGGER.info("No village near spawn, using the vanilla spawn");
            return;
        }

        BlockPos spawn = findSafeSpot(level, ChunkPos.containing(village));

        if (spawn == null) {
            return;
        }

        ServerLevelData settings = event.getSettings();
        settings.setSpawn(LevelData.RespawnData.of(level.dimension(), spawn, 0.0F, 0.0F));
        event.setCanceled(true);

        LOGGER.info("World spawn set at the village near {}", spawn);
    }

    /** Spiral around the village centre, like vanilla does around its spawn. */
    private static BlockPos findSafeSpot(ServerLevel level, ChunkPos centre) {

        int x = 0;
        int z = 0;
        int dx = 0;
        int dz = -1;

        for (int i = 0; i < Mth.square(SPIRAL_SIZE); i++) {

            BlockPos pos = PlayerSpawnFinder.getSpawnPosInChunk(
                    level,
                    new ChunkPos(centre.x() + x, centre.z() + z)
            );

            if (pos != null) {
                return pos;
            }

            if (x == z || (x < 0 && x == -z) || (x > 0 && x == 1 - z)) {
                int old = dx;
                dx = -dz;
                dz = old;
            }

            x += dx;
            z += dz;
        }

        return null;
    }
}
