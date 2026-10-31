package com.jellas.noinventoryfacility;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.EmptyBlockGetter;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ItemClassificationSystem {

    private static final double EPSILON = 1.0E-6;

    /**
     * The answer only depends on the item, so it is computed once.
     * Concurrent because singleplayer runs client and server threads.
     */
    private static final Map<Item, Boolean> CACHE =
            new ConcurrentHashMap<>();

    public static boolean isTwoHanded(ItemStack stack) {

        if (stack.isEmpty()) {
            return false;
        }

        return CACHE.computeIfAbsent(
                stack.getItem(),
                ItemClassificationSystem::compute
        );
    }

    private static boolean compute(Item item) {

        if (!(item instanceof BlockItem blockItem)) {
            return false;
        }

        // Evaluate the default shape in an empty world so the result does
        // not depend on whatever happens to be at (0, 0, 0).
        VoxelShape shape = blockItem.getBlock()
                .defaultBlockState()
                .getShape(EmptyBlockGetter.INSTANCE, BlockPos.ZERO);

        if (shape.isEmpty()) {
            return false;
        }

        AABB bounds = shape.bounds();

        return bounds.getXsize() >= 1.0 - EPSILON
                && bounds.getYsize() >= 1.0 - EPSILON
                && bounds.getZsize() >= 1.0 - EPSILON;
    }
}
