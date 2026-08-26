package com.jellas.noinventoryfacility;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.VoxelShape;

public class ItemClassificationSystem {

    public static boolean isTwoHanded(ItemStack stack, Level level) {

        if (!(stack.getItem() instanceof BlockItem blockItem)) {
            return false;
        }

        Block block = blockItem.getBlock();
        BlockState state = block.defaultBlockState();

        VoxelShape shape = state.getShape(
                level,
                BlockPos.ZERO
        );

        if (shape.isEmpty()) {
            return false;
        }

        AABB bounds = shape.bounds();

        double volume =
                bounds.getXsize()
                        * bounds.getYsize()
                        * bounds.getZsize();

        return volume == 1.0;
    }
}