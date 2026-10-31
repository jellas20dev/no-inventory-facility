package com.jellas.noinventoryfacility.workbench;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.Map;
import java.util.function.Function;

public class WorkbenchBlock extends HorizontalDirectionalBlock implements EntityBlock {

    public static final MapCodec<WorkbenchBlock> CODEC =
            simpleCodec(WorkbenchBlock::new);

    /**
     * Shape for facing = north, matching models/block/workbench.json:
     * the table (top, legs, shelf) spans x -6.5..22.5, almost half a block
     * past each side, and the hide rack along the south edge rises to y = 31.
     */
    private static final Map<Direction, VoxelShape> SHAPES =
            Shapes.rotateHorizontal(Shapes.or(
                    Block.box(-6.5, 0, 0, 22.5, 15.1, 14.3),
                    Block.box(-6.5, 0, 14.3, 22.5, 31, 16)
            ));

    private final Function<BlockState, VoxelShape> shapes;

    public WorkbenchBlock(BlockBehaviour.Properties properties) {
        super(properties);

        this.registerDefaultState(
                this.stateDefinition.any().setValue(FACING, Direction.NORTH)
        );

        this.shapes = this.getShapeForEachState(
                state -> SHAPES.get(state.getValue(FACING))
        );
    }

    @Override
    public MapCodec<WorkbenchBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(
            StateDefinition.Builder<Block, BlockState> builder
    ) {
        builder.add(FACING);
    }

    /** The open side of the bench faces the player who placed it. */
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(
                FACING,
                context.getHorizontalDirection().getOpposite()
        );
    }

    @Override
    protected VoxelShape getShape(
            BlockState state,
            BlockGetter level,
            BlockPos pos,
            CollisionContext context
    ) {
        return this.shapes.apply(state);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new WorkbenchBlockEntity(pos, state);
    }
}
