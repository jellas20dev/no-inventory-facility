package com.jellas.noinventoryfacility.workbench;

import com.jellas.noinventoryfacility.NoInventoryFacility;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.EmptyBlockGetter;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Adds one workbench to the village town centre, so every village has one:
 * it is where a new player starts crafting. Applied to the town centre
 * pieces through data/minecraft/worldgen/template_pool/village/{biome}/town_centers.json.
 *
 * The spot is picked on an open patch of the square's floor, from the
 * whole piece and a seed fixed by the piece position, so every chunk the
 * piece is placed in agrees on the same single spot.
 */
public class VillageWorkbenchProcessor implements StructureProcessor {

    public static final VillageWorkbenchProcessor INSTANCE = new VillageWorkbenchProcessor();

    public static final MapCodec<VillageWorkbenchProcessor> CODEC = MapCodec.unit(INSTANCE);

    private VillageWorkbenchProcessor() {
    }

    @Override
    public MapCodec<VillageWorkbenchProcessor> codec() {
        return CODEC;
    }

    /** Needs the whole piece, not just the chunk being generated. */
    @Override
    public boolean evaluatesEntirePieceState() {
        return true;
    }

    @Override
    public List<StructureBlockInfo> finalizeProcessing(
            ServerLevelAccessor level,
            BlockPos position,
            BlockPos referencePos,
            List<StructureBlockInfo> original,
            List<StructureBlockInfo> processed,
            StructurePlaceSettings settings
    ) {
        Map<BlockPos, BlockState> blocks = new HashMap<>();

        for (StructureBlockInfo info : processed) {
            blocks.put(info.pos(), info.state());
        }

        // Best spots first; looser rules only when a square has none
        // (e.g. the plains fountain, which is mostly water and rim).
        List<BlockPos> candidates = findSpots(processed, blocks, Fit.OPEN_SQUARE);

        if (candidates.isEmpty()) {
            candidates = findSpots(processed, blocks, Fit.FREE_SIDES);
        }

        if (candidates.isEmpty()) {
            candidates = findSpots(processed, blocks, Fit.SPOT_ONLY);
        }

        if (candidates.isEmpty()) {
            return processed;
        }

        // Same piece position + world seed -> same choice in every chunk.
        RandomSource random = RandomSource.create(
                level.getLevel().getSeed() ^ position.asLong()
        );

        BlockPos spot = candidates.get(random.nextInt(candidates.size()));
        Direction facing = Direction.Plane.HORIZONTAL.getRandomDirection(random);

        List<StructureBlockInfo> result = new ArrayList<>(processed);
        result.add(new StructureBlockInfo(
                spot,
                NoInventoryFacility.WORKBENCH.get()
                        .defaultBlockState()
                        .setValue(WorkbenchBlock.FACING, facing),
                null
        ));

        return result;
    }

    private enum Fit {
        /** Neighbours free and standing on the square's floor. */
        OPEN_SQUARE,
        /** Neighbours free (may be at the edge of the square). */
        FREE_SIDES,
        /** Only the spot and the block above it free. */
        SPOT_ONLY
    }

    private static List<BlockPos> findSpots(
            List<StructureBlockInfo> processed,
            Map<BlockPos, BlockState> blocks,
            Fit fit
    ) {
        List<BlockPos> spots = new ArrayList<>();

        for (StructureBlockInfo info : processed) {
            BlockPos spot = info.pos().above();

            if (isFloor(info.state()) && fits(blocks, spot, fit)) {
                spots.add(spot);
            }
        }

        return spots;
    }

    /**
     * Free space for the model, which is two blocks tall and wider than
     * one block.
     */
    private static boolean fits(Map<BlockPos, BlockState> blocks, BlockPos spot, Fit fit) {

        if (!isEmpty(blocks, spot) || !isEmpty(blocks, spot.above())) {
            return false;
        }

        if (fit == Fit.SPOT_ONLY) {
            return true;
        }

        for (Direction side : Direction.Plane.HORIZONTAL) {
            BlockPos next = spot.relative(side);

            if (!isEmpty(blocks, next) || !isEmpty(blocks, next.above())) {
                return false;
            }

            if (fit == Fit.OPEN_SQUARE && !isFloor(blocks.get(next.below()))) {
                return false;
            }
        }

        return true;
    }

    private static boolean isEmpty(Map<BlockPos, BlockState> blocks, BlockPos pos) {
        BlockState state = blocks.get(pos);
        return state == null || state.isAir();
    }

    /** Full-topped ground; dirt paths are 15/16 high but count. */
    private static boolean isFloor(BlockState state) {
        return state != null
                && (state.is(Blocks.DIRT_PATH)
                || Block.isShapeFullBlock(state.getCollisionShape(
                        EmptyBlockGetter.INSTANCE, BlockPos.ZERO)));
    }
}
