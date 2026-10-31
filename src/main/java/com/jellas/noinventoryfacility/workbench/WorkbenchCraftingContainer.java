package com.jellas.noinventoryfacility.workbench;

import net.minecraft.core.NonNullList;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedItemContents;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * The 3x3 grid stored in the workbench. Unlike the vanilla crafting table
 * grid it is persistent, so a player with only two hands can build a recipe
 * one item at a time.
 *
 * Every change is pushed synchronously to the open menus so their result
 * slot is recalculated immediately (shift-click crafting loops on it).
 */
public class WorkbenchCraftingContainer implements CraftingContainer {

    public static final int WIDTH = 3;
    public static final int HEIGHT = 3;

    private final NonNullList<ItemStack> items =
            NonNullList.withSize(WIDTH * HEIGHT, ItemStack.EMPTY);

    private final List<AbstractContainerMenu> menus = new ArrayList<>();

    private final Runnable onChanged;

    public WorkbenchCraftingContainer(Runnable onChanged) {
        this.onChanged = onChanged;
    }

    /** Client-side placeholder: contents arrive through menu sync. */
    public WorkbenchCraftingContainer() {
        this(() -> {});
    }

    public NonNullList<ItemStack> stacks() {
        return items;
    }

    public void addMenu(AbstractContainerMenu menu) {
        menus.add(menu);
    }

    public void removeMenu(AbstractContainerMenu menu) {
        menus.remove(menu);
    }

    private void changed() {
        onChanged.run();

        // Copy: a menu reacting to the change must not break the iteration.
        for (AbstractContainerMenu menu : List.copyOf(menus)) {
            menu.slotsChanged(this);
        }
    }

    @Override
    public int getContainerSize() {
        return items.size();
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack stack : items) {
            if (!stack.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack getItem(int slot) {
        return slot >= 0 && slot < items.size() ? items.get(slot) : ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        return ContainerHelper.takeItem(items, slot);
    }

    @Override
    public ItemStack removeItem(int slot, int count) {
        ItemStack removed = ContainerHelper.removeItem(items, slot, count);

        if (!removed.isEmpty()) {
            changed();
        }

        return removed;
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        items.set(slot, stack);
        changed();
    }

    @Override
    public void setChanged() {
        changed();
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    @Override
    public void clearContent() {
        items.clear();
        changed();
    }

    @Override
    public int getWidth() {
        return WIDTH;
    }

    @Override
    public int getHeight() {
        return HEIGHT;
    }

    @Override
    public List<ItemStack> getItems() {
        return List.copyOf(items);
    }

    @Override
    public void fillStackedContents(StackedItemContents contents) {
        for (ItemStack stack : items) {
            contents.accountSimpleStack(stack);
        }
    }
}
