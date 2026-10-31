package com.jellas.noinventoryfacility.workbench;

import com.jellas.noinventoryfacility.InventorySystem;
import com.jellas.noinventoryfacility.ItemClassificationSystem;
import com.jellas.noinventoryfacility.NoInventoryFacility;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.inventory.ResultSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;

import java.util.Optional;

/**
 * Crafting grid stored in the workbench + result + the player's two hands.
 * No player inventory: in this mod the hands are the only slots.
 */
public class WorkbenchMenu extends AbstractContainerMenu {

    public static final int RESULT_SLOT = 0;
    private static final int GRID_START = 1;
    private static final int GRID_END = 10;
    private static final int HANDS_START = 10;
    private static final int HANDS_END = 12;

    /** Hand slot positions, used by the screen to draw their frames. */
    public static final int MAIN_HAND_X = 71;
    public static final int OFF_HAND_X = 89;
    public static final int HANDS_Y = 92;

    private final WorkbenchCraftingContainer grid;
    private final ResultContainer resultSlots = new ResultContainer();
    private final ContainerLevelAccess access;
    private final Player player;

    /** Client side. */
    public WorkbenchMenu(int containerId, Inventory inventory) {
        this(
                containerId,
                inventory,
                new WorkbenchCraftingContainer(),
                ContainerLevelAccess.NULL
        );
    }

    /** Server side, bound to the workbench's stored grid. */
    public WorkbenchMenu(
            int containerId,
            Inventory inventory,
            WorkbenchCraftingContainer grid,
            ContainerLevelAccess access
    ) {
        super(NoInventoryFacility.WORKBENCH_MENU.get(), containerId);

        this.grid = grid;
        this.access = access;
        this.player = inventory.player;

        this.addSlot(new ResultSlot(player, grid, resultSlots, 0, 124, 35));

        for (int y = 0; y < WorkbenchCraftingContainer.HEIGHT; y++) {
            for (int x = 0; x < WorkbenchCraftingContainer.WIDTH; x++) {
                this.addSlot(new Slot(
                        grid,
                        x + y * WorkbenchCraftingContainer.WIDTH,
                        30 + x * 18,
                        17 + y * 18
                ));
            }
        }

        this.addSlot(new HandSlot(inventory, InventorySystem.MAIN_HAND_SLOT,
                MAIN_HAND_X, HANDS_Y, true));
        this.addSlot(new HandSlot(inventory, Inventory.SLOT_OFFHAND,
                OFF_HAND_X, HANDS_Y, false));

        grid.addMenu(this);

        // Show the result for whatever is already on the bench.
        this.slotsChanged(grid);
    }

    @Override
    public void slotsChanged(Container container) {
        this.access.execute((level, pos) -> {
            if (level instanceof ServerLevel serverLevel) {
                updateResult(serverLevel);
            }
        });
    }

    /** Same as CraftingMenu#slotChangedCraftingGrid. */
    private void updateResult(ServerLevel level) {

        if (!(player instanceof ServerPlayer serverPlayer)) {
            return;
        }

        CraftingInput input = grid.asCraftInput();
        ItemStack result = ItemStack.EMPTY;

        Optional<RecipeHolder<CraftingRecipe>> recipe = level.getServer()
                .getRecipeManager()
                .getRecipeFor(RecipeType.CRAFTING, input, level);

        if (recipe.isPresent()
                && resultSlots.setRecipeUsed(serverPlayer, recipe.get())) {

            ItemStack assembled = recipe.get().value().assemble(input);

            if (assembled.isItemEnabled(level.enabledFeatures())) {
                result = assembled;
            }
        }

        resultSlots.setItem(0, result);
        this.setRemoteSlot(RESULT_SLOT, result);
        serverPlayer.connection.send(new ClientboundContainerSetSlotPacket(
                this.containerId,
                this.incrementStateId(),
                RESULT_SLOT,
                result
        ));
    }

    @Override
    public ItemStack quickMoveStack(Player player, int slotIndex) {

        Slot slot = this.slots.get(slotIndex);

        if (!slot.hasItem()) {
            return ItemStack.EMPTY;
        }

        ItemStack stack = slot.getItem();
        ItemStack original = stack.copy();

        if (slotIndex == RESULT_SLOT) {
            stack.getItem().onCraftedBy(stack, player);

            if (!this.moveItemStackTo(stack, HANDS_START, HANDS_END, false)) {
                return ItemStack.EMPTY;
            }

            slot.onQuickCraft(stack, original);

        } else if (slotIndex < GRID_END) {
            if (!this.moveItemStackTo(stack, HANDS_START, HANDS_END, false)) {
                return ItemStack.EMPTY;
            }

        } else if (!this.moveItemStackTo(stack, GRID_START, GRID_END, false)) {
            return ItemStack.EMPTY;
        }

        if (stack.isEmpty()) {
            slot.setByPlayer(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }

        if (stack.getCount() == original.getCount()) {
            return ItemStack.EMPTY;
        }

        slot.onTake(player, stack);

        // Whatever did not fit in the hands goes to the ground.
        if (slotIndex == RESULT_SLOT) {
            player.drop(stack, false);
        }

        return original;
    }

    @Override
    public boolean canTakeItemForPickAll(ItemStack carried, Slot target) {
        return target.container != this.resultSlots
                && super.canTakeItemForPickAll(carried, target);
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(access, player, NoInventoryFacility.WORKBENCH.get());
    }

    /** The grid stays on the bench: nothing is returned or dropped. */
    @Override
    public void removed(Player player) {
        super.removed(player);
        grid.removeMenu(this);
    }

    /** A hand: one item, and the two-handed rule of the mod. */
    private static class HandSlot extends Slot {

        private final boolean mainHand;

        HandSlot(Inventory inventory, int slot, int x, int y, boolean mainHand) {
            super(inventory, slot, x, y);
            this.mainHand = mainHand;
        }

        @Override
        public int getMaxStackSize() {
            return mainHand ? ItemClassificationSystem.MAX_TOWER : 1;
        }

        /** Building blocks stack as a tower in the main hand. */
        @Override
        public int getMaxStackSize(ItemStack stack) {
            return mainHand ? ItemClassificationSystem.maxInHand(stack) : 1;
        }

        @Override
        public boolean mayPlace(ItemStack stack) {

            Inventory inventory = (Inventory) this.container;

            ItemStack otherHand = mainHand
                    ? inventory.getItem(Inventory.SLOT_OFFHAND)
                    : inventory.getItem(InventorySystem.MAIN_HAND_SLOT);

            if (ItemClassificationSystem.isTwoHanded(otherHand)) {
                return false;
            }

            if (ItemClassificationSystem.isTwoHanded(stack)) {
                // Heavy items are carried in the main hand, both hands busy.
                return mainHand && otherHand.isEmpty();
            }

            return true;
        }
    }
}
