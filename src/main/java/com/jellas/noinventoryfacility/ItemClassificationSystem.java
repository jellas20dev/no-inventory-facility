package com.jellas.noinventoryfacility;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.EmptyBlockGetter;
import net.minecraft.world.level.block.EntityBlock;
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

    /**
     * Heavy items that are not full cubes (doors, slabs, fences, functional
     * blocks...). Data-driven: data/noinventoryfacility/tags/item/two_handed.json
     */
    public static final TagKey<Item> TWO_HANDED = TagKey.create(
            Registries.ITEM,
            Identifier.fromNamespaceAndPath(
                    NoInventoryFacility.MODID,
                    "two_handed"
            )
    );

    /**
     * Full-cube blocks that must NOT be carried as a tower (work tables and
     * other functional blocks). Blocks with a block entity (chests, furnaces,
     * barrels...) and everything in two_handed are already excluded.
     * Data-driven: data/noinventoryfacility/tags/item/not_stackable.json
     */
    public static final TagKey<Item> NOT_STACKABLE = TagKey.create(
            Registries.ITEM,
            Identifier.fromNamespaceAndPath(
                    NoInventoryFacility.MODID,
                    "not_stackable"
            )
    );

    /** How many building blocks fit in the arms, as a tower. */
    public static final int MAX_TOWER = 4;

    /**
     * Common building blocks (stone, wool, planks, logs...) can be carried
     * stacked up to {@link #MAX_TOWER}.
     */
    public static boolean isStackable(ItemStack stack) {

        if (stack.isEmpty()
                || stack.is(TWO_HANDED)
                || stack.is(NOT_STACKABLE)
                || stack.getMaxStackSize() < 2) {
            return false;
        }

        if (!(stack.getItem() instanceof BlockItem blockItem)
                || blockItem.getBlock() instanceof EntityBlock) {
            return false;
        }

        return isFullCube(stack.getItem());
    }

    /** Most items a hand can hold of this stack. */
    public static int maxInHand(ItemStack stack) {
        return isStackable(stack)
                ? Math.min(MAX_TOWER, stack.getMaxStackSize())
                : 1;
    }

    public static boolean isTwoHanded(ItemStack stack) {

        if (stack.isEmpty()) {
            return false;
        }

        // Not cached: tags can change on /reload.
        if (stack.is(TWO_HANDED)) {
            return true;
        }

        return isFullCube(stack.getItem());
    }

    private static boolean isFullCube(Item item) {
        return CACHE.computeIfAbsent(item, ItemClassificationSystem::compute);
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
